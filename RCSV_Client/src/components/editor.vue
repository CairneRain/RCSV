<template>
    <div id="window">
      <div id="leftWindow" ref="leftWindow">
        <div id="topController">
          <div id="leftTopController">
            <div v-if="isSend">
              <!-- 前进后退的功能 -->
              <el-button-group>
                <el-button :disabled="isLoading" type="primary" round v-on:click="onBackClick"><i class="el-icon-arrow-left el-icon--right"></i>Back</el-button>
                <el-button :disabled="isLoading" :type="autoClick?'warning':'primary'" round v-on:click="onAutoClick">Auto</el-button>
                <el-button :disabled="isLoading" type="primary" round v-on:click="onNextClick">Next<i class="el-icon-arrow-right el-icon--right"></i></el-button>
              </el-button-group>
            </div>
            <div v-if="!isSend" id="switchSelector">
              <!-- language selector -->
              <el-select v-model="cmOptions.mode" @change="changeMode" placeholder="choose language">
                <el-option v-for="mode in modes" :key="mode.value" :label="mode.label" :value="mode.value" :disabled="mode.disabled">
                  <span style="float: left">{{ mode.label }}</span>
                  <span style="float: right; color: #c0c4cc; font-size: 13px">{{ mode.hint }}</span>
                </el-option>
              </el-select>
              <!-- ai提示的开关 -->
              <el-switch v-model="isOpenHint" active-text="Hint" @change="AISwitch=false"></el-switch>
              <el-tooltip :content="'Open the assistant hints from AI. It is experimental'" placement="top" :disabled="!isOpenHint" >
                <el-switch v-model="AISwitch" @change="onAiSwitchChange" active-text="AI+" inactive-text="" :disabled="!isOpenHint"></el-switch>
              </el-tooltip>
              <el-select v-if="AISwitch" v-model="chosenScope" @change="changeAI" placeholder="choose prediction scope">
                <el-option v-for="scope in wiseScopes" :key="scope.value" :label="scope.label" :value="scope.value">
                  <span>{{scope.label}}</span>
                  <span></span>
                </el-option>
              </el-select>
            </div>
          </div>

          <div id="rightTopController" ref="stepButton">
            <!-- 主题选择 -->
            <el-select v-model="cmOptions.theme" placeholder="choose theme">
              <el-option v-for="theme in themes" :key="theme.value" :label="theme.label" :value="theme.value"></el-option>
            </el-select>  
          </div>
        </div>
        <!-- code mirror -->
        <codemirror
          ref="cm" 
          id="code" 
          v-model="code" 
          :options="cmOptions"
        ></codemirror>
        <div id="bottomController">
          <div v-if="isSend">
            <!-- 行提示 -->
            <div class="legend">
              <img style="width:10%" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGgAAABrCAMAAACsVjc9AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAPUExURQAAAP8AAP8AAP8AAP8AAGoEBYIAAAAEdFJOUwAsgOAZTaMtAAAACXBIWXMAADLAAAAywAEoZFrbAAABlklEQVRoQ73aSW6DUBAAUSfm/mcOQwF/RCLq6tqYP6ifLO8sPu/74VPvmyUtWdKSJS1Z0grlSBuUIu1QhnRACRKQL+H4EsyaLKFsuRLIniphHJkSBIkSwpknAVxpEvPvLInxRZLE9DJHYniVIjG7zpAY3SRITG6LlxjcFS4xt+9Z+n0dYwc9StyJ6UniSlAPEjeimktcCGsqcR7XTOI4sInEaWRjicPQhhJnsY0kjoIbSJxE10schNdJ7MfXSmwLNRK7RrXEplIlsedUSmxJFRI7VrfEhtYlsfY6JZZiSKzMDomF2i7x7LZJPMqtEk9uWd8o6zfanQTocHwIR4dOx4YuR4Zux4UKR4VKx4QqR4Rqx4MaR4Nax4I6R4J6x4EGjgKNHAMaOgI0duKhiRMOzZxoaOoEQ3MnFnpw8v5TfR9T+4KdKRTtzKBwZwLFO2NIcIaQ4YwgxRlAjtNDktNBltNCmtNAnlNDolNBplNCqlNArnNDsnNBtnNCugPkOweU4OxQhrNBKc4K5Th5b3VmOf968/bz+QPrxFSHh4yn5wAAAABJRU5ErkJggg=="/>
              the current line
            </div>
            <div class="legend">
              <img style="width:10%" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGgAAABqCAMAAABnCuSYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAbUExURQAAAK25zK25y6u3ya25yq66yq25ya25yq25ypZYAuEAAAAIdFJOUwBtf4CGv9/fAOg2XQAAAAlwSFlzAAAywAAAMsABKGRa2wAAAZpJREFUaEO92tuKwkAQRdGMzsX//+LR5CTpS1VAObv2i93VIQtEEIzL+33pFe/+rQXd/VEk3R5F0hOqkV5QibRCFdIGFUiCeGmHcOmAaOmEYKmBWKmFUKmDSKmHQGmAOGmEMOmu+59B0gxBUgAxUgQhUggRUgwBUgL5pQyySyl0Lf283a9uG3Ql6RJTF5KucJVLusBWKuncVybp2Fgi6dRZLOnQWijpzFsk6chcIOnE3SzpwN4kae5vlDQGGiRNiXpJQ6RO0oyplTSCaiRNqE5JA6xD0p5rl7QFk6Qd2SZpg7ZKWrO9JC3hnpJWcFVQ1VtX9WFYnQJoc3hIDg7tDg0dDgydDgs1Dgq1Dgl1Dgj1DgcNDgaNDgVNDgTNDgMFDgJFDgGFDgDFjh9KHDuUOW4odcxQ7nzwU+efbhp04XzQ9LTlyOvkkNlJIbeTQXYngfxODAFOCBFOBCFOADHO/LQFciaIckYIcwaIc3oIdDqIdFoIdRqIdU4Idg6IdnYIdwTxzgYVOCtU4by+JkqcJ1TjLLci56M/xC7LP4aKprVo/E2hAAAAAElFTkSuQmCC"/>
              the next line
            </div>
          </div>
          <!-- 步骤滑动栏 -->
          <div v-if="isSend" id="slider" ref="slider"><el-slider v-model="stepNum" :max=totalStep :min=1 :format-tooltip="sliderTipFormat" @input="changeDebugInfo($event-1)"></el-slider></div>
          <!-- 保存（发送给后端接口）和修改（右侧等待动画） -->
          <el-button type="primary" round :loading="isLoading" v-on:click="onSendClick">{{sendButtonMsg}}</el-button>
        </div>
       </div>
      <div v-if="isSend" id="rightWindow" ref="rightWindow">
        <div v-if="isLoading">
          <h1 class="animate__animated Loading animate__infinite">Loading</h1>
        </div>
        <div v-if="!isLoading" id="stepInfo">
        <b style="font-size:20px" 
            @click="onConsoleClick()">
            Step {{stepNum}}
        </b>
        </div>
        <div v-if="!isLoading" id="stackInfo">
          <div id="callStackInfo">
            <h3>Call Stack</h3>
            <hr/>
            <div class="stack" v-for="stack in currentStackList" :key="stack.callMethodInfo" ref="callStackInfo">
              <el-popover placement="left" width="250" trigger="hover"
              :title="stack.callMethodInfo.split('(')[0]">  
                <el-descriptions title="" :column="1" border>
                  <el-descriptions-item label="Class Name">{{stack.lineInfo.split(':')[0]}}</el-descriptions-item>
                  <el-descriptions-item label="Line Number">{{Number(stack.lineInfo.split(':')[1])-1}}</el-descriptions-item>
                  <el-descriptions-item label="Method">{{stack.callMethodInfo.split('(')[0]}}</el-descriptions-item>
                  <el-descriptions-item v-if="stack.callMethodInfo.split('(')[1].split(')')[0]!=''" label="Parameters">{{stack.callMethodInfo.split('(')[1].split(')')[0]}}</el-descriptions-item>
                </el-descriptions>
                <el-button type="primary" slot="reference"
                  v-on:click="onCallStackInfoClick(stack)" >
                  {{stack.callMethodInfo.split('(')[0]}}
                </el-button>
              </el-popover>
              <hr/>
            </div>
          </div>
          <div  id="variableInfo">
            <h3>Variable</h3>
            <hr/>
            <!-- detail information dialog -->
            <el-dialog v-if="chosenVariable!=null" 
            :title="'Detail Information of '+ chosenVariable.variableName" 
            :visible.sync="isVariableDialogShow"
            width="70%"
            center>
              <!-- detail information -->
              <el-descriptions title="" :column="1" border class="variableDetail">
                <el-descriptions-item label="Variable Name">{{chosenVariable.variableName}}</el-descriptions-item>
                <el-descriptions-item label="Type Name">{{chosenVariable.typeName}}</el-descriptions-item>
                <el-descriptions-item label="Value">{{chosenVariable.value}}</el-descriptions-item>
                <el-descriptions-item v-if="this.cmOptions.mode!='python'" label="is Argument">{{chosenVariable.isArgument?"true":"false"}}</el-descriptions-item>
              </el-descriptions>
              <!-- popup in detail dialog -->
              <div v-if="chosenVariable.fields.length>0">
                <hr/>
                <h3>Fields:</h3>
              </div>
              <div class="field-container">
                <div v-for="field in chosenVariable.fields" :key="field.varName">
                  <el-popover
                  placement="top"
                  :title="field.variableName"
                  trigger="hover">
                    <el-button slot="reference"
                      type="primary" class="field-item"
                      v-on:click="onDialogVariableClick(field)" >
                      {{field.variableName}}
                    </el-button>
                    <el-descriptions title="" :column="1" border>
                      <el-descriptions-item label="Variable Name">{{field.variableName}}</el-descriptions-item>
                      <el-descriptions-item label="Type Name">{{field.typeName}}</el-descriptions-item>
                      <el-descriptions-item label="Value">{{field.value}}</el-descriptions-item>
                      <!-- <el-descriptions-item label="is Argument">{{field.isArgument?"√":"×"}}</el-descriptions-item> -->
                    </el-descriptions>
                  </el-popover>
                </div>
              </div>
              <!-- detail footer -->
              <span v-if="isDialogBackShow" slot="footer" class="dialog-footer">
                <el-button @click="onDialogBackClick()">back</el-button>
              </span>
            </el-dialog>
            <!-- variable information -->
            <div class="variable-container" >
              <div v-for="variable in variableInfo" :key="variable.varName">
                <el-popover placement="top" trigger="hover"
                :title="variable.variableName">
                  <el-button slot="reference"
                    v-if="!variable.isArgument" type="primary" class="variable-item" 
                    v-on:click="onVariableClick(variable)" >
                    {{variable.variableName}}
                  </el-button>
                  <el-button slot="reference"
                    v-if="variable.isArgument" type="warning" class="variable-item" 
                    v-on:click="onVariableClick(variable)" >
                    {{variable.variableName}}
                  </el-button>
                  <div>
                    <el-descriptions title="" :column="1" border>
                      <el-descriptions-item label="Variable Name">{{variable.variableName}}</el-descriptions-item>
                      <el-descriptions-item label="Type Name">{{variable.typeName}}</el-descriptions-item>
                      <el-descriptions-item label="Value">{{variable.value}}</el-descriptions-item>
                    </el-descriptions>
                  </div>
                </el-popover>
              </div>
            </div>
            <!-- {{this.variableInfo}} -->
          </div>
        </div>
      </div>
    </div>
</template>

<script>
  import codemirror from "./js/editor.js";
  export default codemirror;
</script>

<style>
@import "./css/editor.css";
</style>