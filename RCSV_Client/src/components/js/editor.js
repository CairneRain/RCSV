import {codemirror} from 'vue-codemirror'
import 'codemirror/lib/codemirror.css'
import 'codemirror/lib/codemirror.js'//Cr is pig, lwl is very beautiful. They love each other. So lwl is a girl who loves animals and is very kind, cute, nice and smart. One day, she said, cr's meat is very delicious. 
import 'codemirror/theme/darcula.css'// 配置里面也需要theme设置
import 'codemirror/theme/panda-syntax.css'// 配置里面也需要theme设置
import 'codemirror/theme/abbott.css'// 配置里面也需要theme设置
import 'codemirror/theme/cobalt.css'// 配置里面也需要theme设置
import 'codemirror/theme/elegant.css'// 配置里面也需要theme设置
import 'codemirror/theme/juejin.css'// 配置里面也需要theme设置
import 'codemirror/theme/liquibyte.css'// 配置里面也需要theme设置
import 'codemirror/theme/lucario.css'// 配置里面也需要theme设置
import 'codemirror/theme/night.css'// 配置里面也需要theme设置
import 'codemirror/theme/neo.css'// 配置里面也需要theme设置
import 'codemirror/theme/seti.css'// 配置里面也需要theme设置
import 'codemirror/theme/ssms.css'// 配置里面也需要theme设置
import 'codemirror/theme/solarized.css'// 配置里面也需要theme设置
import 'codemirror/theme/twilight.css'// 配置里面也需要theme设置
import 'codemirror/theme/yeti.css'// 配置里面也需要theme设置
import 'codemirror/theme/yonce.css'// 配置里面也需要theme设置
import 'codemirror/theme/zenburn.css'// 配置里面也需要theme设置
import 'codemirror/theme/material.css'// 配置里面也需要theme设置
import 'codemirror/theme/icecoder.css'// 配置里面也需要theme设置
import 'codemirror/theme/idea.css'// 配置里面也需要theme设置
import 'codemirror/theme/isotope.css'// 配置里面也需要theme设置
import 'codemirror/keymap/sublime.js' // sublime编辑器效果
import 'codemirror/mode/python/python.js' // 配置里面也需要mode设置为vue
import 'codemirror/mode/go/go.js' // 配置里面也需要mode设置为vue
import 'codemirror/mode/javascript/javascript.js' // 配置里面也需要mode设置为vue
import 'codemirror/mode/clike/clike.js' // 配置里面也需要mode设置为vue
import 'codemirror/addon/selection/selection-pointer' // 光标行背景高亮，配置里面也需要styleActiveLine设置为true
import 'codemirror/addon/selection/active-line'//我爱洗澡身体好好，哦哦哦~w
import 'codemirror/addon/hint/show-hint.css'; // 输入hint
import 'codemirror/addon/hint/show-hint';
import 'codemirror/addon/hint/sql-hint';
import { Notification } from 'element-ui';

export default {
  components: {
    codemirror
  },
  mounted(){
    //获取初始code
    this.axios.post('/java/debug/init',this.$qs.stringify({
      'lang':this.cmOptions.mode
    })).then(res => {
      this.code=res.data
    }).catch(err=>{
      this.code=this.javaCodeSample;
      console.log(err)
    })

    //codemirror的key up事件
    document.getElementById("code").addEventListener("keydown",event=>{
      var x = event.key;
      //禁用回车,在extraKeys调用
      if (x == "Enter") {
        event.preventDefault();
      }
      //提示的开启
      if(!this.isOpenHint){
        return
      }
      var reg = /^[a-zA-Z0-9]$/
      if(reg.test(x)&&!event.ctrlKey){
        this.$refs.cm.codemirror.showHint()
      }
      else if(x=="ArrowDown"||x=="ArrowUp"){
        //选择
      }
      else{
        this.$refs.cm.codemirror.closeHint()
      }
    })

  },
  data () {
    return {
      isSend:false,
      // button/switch/selector
      AISwitch:false,
      AIScope:1,
      isOpenHint:false,
      sendButtonMsg:"Send",
      isLoading:false,
      //slider
      stepNum:1,
      totalStep:10,
      //auto click
      autoClick:false,
      timer:null,
      speed:1000,
      //call stack/variable
      debugInfo:null,
      lineInfo:null,
      lineNum:null,
      currentStackList:[],
      variableInfo:"variable",
      chosenStack:0,
      isVariableDialogShow:false,
      chosenVariable:null,
      previousVariables:[],
      isDialogBackShow:false,
      // console information
      consoleInfo:null,
      consoleInfoShow:true,
      consoleInfoNotification:null,
      // codeMirror
      codeMirror: null,
      active:[],
      code: null,
      originCode:null,
      javaCodeSample:'//Java Sample\n',
      pythonCodeSample:'//Python Sample',
      cmOptions: {
        tabSize: 4, // tab 宽度
        indentUnit: 4, // 缩进单位(默认2)
        theme: 'panda-syntax', // 主题样式
        lineNumbers: true, // 是否显示行数
        lineWrapping: true, // 是否自动换行
        gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter", "breakpoints"],
        styleActiveLine: true, // line选择是是否加亮
        matchBrackets: true, // 括号匹配
        mode: 'text/x-java', // 代码高亮
        readOnly: false, // 只读
        extraKeys: {
          "Enter":(cm)=>{
            cm.execCommand("newlineAndIndent")
          },
        }, 
        hintOptions: {
          // 避免由于提示列表只有一个提示信息时，自动填充
          completeSingle: false,
          // hint: this.showAiHint,
          // 不同的语言支持从配置中读取自定义配置 sql语言允许配置表和字段信息，用于代码提示
          // tables: {
          //   "table1": ["c1", "c2"],
          // },
        },
      },
      //下拉框
      modes: [{
          value: 'text/x-java',
          label: 'Java'
        }, {
          value: 'python',
          label: 'Python'
        },{
          value: 'text/x-csrc',
          label: 'C',
          disabled:true,
          hint: 'Developing'
        },{
          value:'text/x-kotlin',
          label:'Kotlin',
          disabled:true,
          hint: 'Developing'
        },{
          value:'javascript',
          label:'Javascript',
          disabled:true,
          hint: 'Developing'
        },{
          value: 'go',
          label: 'Go',
          disabled:true,
          hint: 'Developing'
        }
      ],
      themes:[
        {
          value:'panda-syntax',
          label:'Panda Syntax'
        },
        {
          value: 'darcula',
          label: 'Darcula'
        },
        {
          value:'abbott',
          label:'Abbott'
        },
        {
          value:'cobalt',
          label:'Cobalt'
        },
        {
          value:'elegant',
          label:'Elegant'
        },
        {
          value:'juejin',
          label:'Juejin'
        },
        {
          value:'liquibyte',
          label:'Liquibyte'
        },
        {
          value:'lucario',
          label:'Lucario'
        },
        {
          value:'night',
          label:'Night'
        },
        {
          value:'neo',
          label:'Neo'
        },
        {
          value:'seti',
          label:'Seti'
        },
        {
          value:'ssms',
          label:'Ssms'
        },
        {
          value:'solarized',
          label:'Solarized'
        },
        {
          value:'twilight',
          label:'Twilight'
        },
        {
          value:'yeti',
          label:'Yeti'
        },
        {
          value:'yonce',
          label:'Yonce'
        },
        {
          value:'zenburn',
          label:'Zenburn'
        },
        {
          value:'material',
          label:'Material'
        },
        {
          value:'icecoder',
          label:'Icecoder'
        },
        {
          value:'idea',
          label:'Idea'
        },
        {
          value:'isotope',
          label:'Isotope'
        }
      ],
      // AI hints
      isOpen:false,
      chosenScope:1,
      wiseScopes:[{
        value:1,
        label:'Word-Wise'
      },{
        value:2,
        label:'Line-Wise'
      },{
        value:3,
        label:'Prev-All'
      }]
    }
  },
  methods:{
    // 修改编辑器的语法配置
    changeMode (val) {
        if(val=="text/x-java"){
          this.code=this.javaCodeSample;
          this.axios.post('/java/debug/init',this.$qs.stringify({
            'lang':this.cmOptions.mode
          })).then(res => {
            this.code=res.data
          }).catch(err=>{
            this.code=this.javaCodeSample;
            console.log(err)
          })
        }
        else if(val=="python"){
          this.code=this.pythonCodeSample;
          this.axios.get('/python/debug/getSampleCode').then(res => {
            this.code=res.data
          }).catch(err=>{
            this.code=this.pythonCodeSample;
            console.log(err)
          })
        }
    },
    // ai 提示
    changeAI(val){
      this.AIScope=val
    },
    showAiHint(doc){
      let cursor = doc.getCursor();
      let token = doc.getTokenAt(cursor)
       // 词级(默认)
      var inputData=token.string
      // 行级
      if(this.chosenScope==2){
        inputData=doc.getLine(cursor.line)
      }
      // 之前全部
      if(this.chosenScope==3){
        inputData=doc.getRange(
          {line:0,ch:0},
          {line:cursor.line,ch:token.end}
        )
      }
      console.log({'inputData':inputData,
                    'lang':this.cmOptions.mode,
                    'scope':this.AIScope})
      // 发送
      return this.axios.post('/python/hint/getHints',{
          'inputData':inputData,
          'lang':this.cmOptions.mode,
          'scope':this.AIScope
      }).then(res => {
        var predictions=[]
        res.data.map((item)=>{predictions.push(Object.assign(item,{render:this.hintRender}))})
        return {
          list: predictions,
          from: {ch: token.start, line: cursor.line},
          to: {ch: token.end, line: cursor.line}
        }
      })
      .catch(err=>{
        console.log(err)
      })
    },
    // 提示样式
    hintRender(element, self, data) {
      let div = document.createElement("div");
      div.setAttribute("class", "autocomplete-div");
    
      let divText = document.createElement("div");
      divText.setAttribute("class", "autocomplete-name");
      divText.innerText = data.displayText;
    
      let divInfo = document.createElement("div");
      divInfo.setAttribute("class", "autocomplete-hint");
      divInfo.innerText = data.displayInfo;
    
      div.appendChild(divText);
      div.appendChild(divInfo);
      element.appendChild(div);
    },
    // AI switch click
    onAiSwitchChange(){
      //变换提示方式
      if(this.AISwitch){
        this.$refs.cm.codemirror.options.hintOptions.hint=this.showAiHint
      }
      else{
        delete this.$refs.cm.codemirror.options.hintOptions.hint
      }
    },
    // 滑动条提示
    sliderTipFormat(val){
      return "step "+val
    },
    //点击Send键
    onSendClick(){
      this.isSend=!this.isSend
      if(this.isSend){//send
        // console.log(this.$refs.cm.codemirror)
        // this.$refs.cm.codemirror.setLineClass(4, 'background', 'line-error')
        //change on code mirror
        this.originCode=this.code
        this.code=this.code.trim()
        this.sendButtonMsg="Revise"
        this.$refs.cm.codemirror.setOption("readOnly","nocursor")
        this.$refs.cm.codemirror.setOption("styleActiveLine",false)
        this.$refs.cm.codemirror.setOption("matchBrackets",false)
        this.cleanActiveLine(this.$refs.cm.codemirror)
        //change on page
        this.$refs.leftWindow.style.width="50%"
        this.stepNum=1
        //Send Request
        this.isLoading=true
        this.axios.post('/'+this.getLang(this.cmOptions.mode)+'/debug/run',
        {
            'code':this.code
        }).then(res => {
            this.isLoading=false
            this.debugInfo = res.data.stack
            this.totalStep=res.data.stack.length
            this.consoleInfo=res.data.console
            this.consoleInfoNotification=this.$notify({
              title: 'Console Output',
              dangerouslyUseHTMLString: true,
              message: "",
              duration: 0,
              showClose: false
            })
            if(this.consoleInfo.exitValue!=0){
              throw new Error("compile error")
            }
            // console.log(this.consoleInfo)
        }).then(res=>{
          this.changeDebugInfo(0)//step one
        })
        .catch(err=>{
          console.log(err)
          // revert to unclick
          this.onSendClick()
          //compile error
          if(this.consoleInfo && this.consoleInfo.exitValue!=0){
            this.$notify.error({
              showClose: true,
              message: this.toHTML(this.consoleInfo.stderr),
              duration:0,
              dangerouslyUseHTMLString: true,
            });
            this.$message.error('Compile Error, Please modify the codes')
          }
          //other error
          else{
            this.$message.error('Error occurs, Please try again')
          }
        })
      }
      else{//before send
        //change on code mirror
        this.code=this.originCode
        this.sendButtonMsg="Send"
        this.$refs.cm.codemirror.setOption("readOnly",false)
        this.$refs.cm.codemirror.setOption("styleActiveLine",true)
        this.$refs.cm.codemirror.setOption("cursorHeight",1)
        this.$refs.cm.codemirror.setOption("matchBrackets",true)
        //change on page
        this.$refs.leftWindow.style.width="70%"
        this.isLoading=false
        Notification.closeAll()
        this.cleanActiveLine(this.$refs.cm.codemirror)
      }
    },
    //step control
    onBackClick(){
      if(this.stepNum>1){
        this.stepNum-=1
        this.changeDebugInfo(this.stepNum-1)
      }
    },
    onNextClick(){
      if(this.stepNum<this.totalStep){
        this.stepNum+=1
        this.changeDebugInfo(this.stepNum-1)
      }
    },
    onAutoClick(){
      this.autoClick=!this.autoClick
      //reset if at last step
      if(this.stepNum>=this.totalStep) this.stepNum=0
      if(this.autoClick){
        this.timer=setInterval(()=>{
          this.stepNum+=1
          //step if at last step
          if(this.stepNum>=this.totalStep){
            clearInterval(this.timer)
            this.autoClick=false
            return
          }
          this.changeDebugInfo(this.stepNum-1)
        },this.speed)
      }
      else{
        clearInterval(this.timer)
      }
    },
    //call stack button
    onCallStackInfoClick(stack){
      var idx=this.currentStackList.indexOf(stack)
      this.$set(stack, 'showDetail', true)
      this.chosenStack=idx
      this.changeDebugInfo(this.stepNum-1)
    },
    //variable button
    onVariableClick(variable){
      this.chosenVariable=variable
      this.previousVariables=[]
      this.isVariableDialogShow=true
      // this.$set(variable, 'showDetail', true)
    },
    onDialogVariableClick(variable){
      this.previousVariables.push(this.chosenVariable)
      this.isDialogBackShow=true
      this.chosenVariable=variable
    },
    onDialogBackClick(){
      this.chosenVariable=this.previousVariables.pop()
      if(this.previousVariables.length==0){
        this.$set(this,'isDialogBackShow', false)
      }
    },
    //change debug information
    changeDebugInfo(step){
      if(this.debugInfo!=null && step<=this.debugInfo.length){
        this.currentStackList=this.debugInfo[step]
        
        this.consoleInfoNotification.message=this.setConsoleHTML(this.currentStackList[0].console)
        // console.log("currentStackList:",this.currentStackList)
        
        this.lineInfo=this.debugInfo[step][0].lineInfo
        this.nextLineInfo = step+1<this.totalStep ? this.debugInfo[step+1][0].lineInfo : null
        this.lineNum=Number(this.lineInfo.split(':')[1])
        this.nextLineNum=Number(this.nextLineInfo?this.nextLineInfo.split(':')[1]:null)
        
        //change to choosen stack
        this.variableInfo=this.debugInfo[step][this.chosenStack].values
        // console.log(this.variableInfo)
        // console.log(this.$refs.callStackInfo)

        //code highlight
        const bias=this.getLang(this.cmOptions.mode)=='java' ? 2 : 1
        this.displayLine(this.lineNum-bias,this.nextLineNum-bias)
        this.scrollTo(this.lineNum)
      }
    },
    displayLine(current,next){
      if(next>0 && current!=next){
        this.setActiveLine(this.$refs.cm.codemirror,[current,next])
      }
      else{
        this.setActiveLine(this.$refs.cm.codemirror,[current])
      }
    },
    setActiveLine(cm,ranges){
      //clean
      this.cleanActiveLine(cm)
      //new active line
      for (var i = 0; i < ranges.length; i++) {
        var range = ranges[i];
        var line = cm.getLineHandleVisualStart(range);
        this.active.push(line)
      }
      // current line
      cm.addLineClass(this.active[0], "wrap", "CodeMirror-activeline");
      cm.addLineClass(this.active[0], "background", "CodeMirror-red-background");
      cm.addLineClass(this.active[0], "gutter", "CodeMirror-activeline-gutter");
      var marker = document.createElement("div");
      marker.innerHTML = "<img style=\"width:100%\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGgAAABrCAMAAACsVjc9AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAPUExURQAAAP8AAP8AAP8AAP8AAGoEBYIAAAAEdFJOUwAsgOAZTaMtAAAACXBIWXMAADLAAAAywAEoZFrbAAABlklEQVRoQ73aSW6DUBAAUSfm/mcOQwF/RCLq6tqYP6ifLO8sPu/74VPvmyUtWdKSJS1Z0grlSBuUIu1QhnRACRKQL+H4EsyaLKFsuRLIniphHJkSBIkSwpknAVxpEvPvLInxRZLE9DJHYniVIjG7zpAY3SRITG6LlxjcFS4xt+9Z+n0dYwc9StyJ6UniSlAPEjeimktcCGsqcR7XTOI4sInEaWRjicPQhhJnsY0kjoIbSJxE10schNdJ7MfXSmwLNRK7RrXEplIlsedUSmxJFRI7VrfEhtYlsfY6JZZiSKzMDomF2i7x7LZJPMqtEk9uWd8o6zfanQTocHwIR4dOx4YuR4Zux4UKR4VKx4QqR4Rqx4MaR4Nax4I6R4J6x4EGjgKNHAMaOgI0duKhiRMOzZxoaOoEQ3MnFnpw8v5TfR9T+4KdKRTtzKBwZwLFO2NIcIaQ4YwgxRlAjtNDktNBltNCmtNAnlNDolNBplNCqlNArnNDsnNBtnNCugPkOweU4OxQhrNBKc4K5Th5b3VmOf968/bz+QPrxFSHh4yn5wAAAABJRU5ErkJggg==\"/>"
      cm.setGutterMarker(this.active[0], "breakpoints", cm.lineInfo(this.active[0]).gutterMarkers ? null : marker);
      // next line
      if(this.active[1]){
        cm.addLineClass(this.active[1], "wrap", "CodeMirror-activeline");
        cm.addLineClass(this.active[1], "background", "CodeMirror-blue-background");
        cm.addLineClass(this.active[1], "gutter", "CodeMirror-activeline-gutter");
        var marker = document.createElement("div");
        marker.innerHTML = "<img style=\"width:100%\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGgAAABqCAMAAABnCuSYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAbUExURQAAAK25zK25y6u3ya25yq66yq25ya25yq25ypZYAuEAAAAIdFJOUwBtf4CGv9/fAOg2XQAAAAlwSFlzAAAywAAAMsABKGRa2wAAAZpJREFUaEO92tuKwkAQRdGMzsX//+LR5CTpS1VAObv2i93VIQtEEIzL+33pFe/+rQXd/VEk3R5F0hOqkV5QibRCFdIGFUiCeGmHcOmAaOmEYKmBWKmFUKmDSKmHQGmAOGmEMOmu+59B0gxBUgAxUgQhUggRUgwBUgL5pQyySyl0Lf283a9uG3Ql6RJTF5KucJVLusBWKuncVybp2Fgi6dRZLOnQWijpzFsk6chcIOnE3SzpwN4kae5vlDQGGiRNiXpJQ6RO0oyplTSCaiRNqE5JA6xD0p5rl7QFk6Qd2SZpg7ZKWrO9JC3hnpJWcFVQ1VtX9WFYnQJoc3hIDg7tDg0dDgydDgs1Dgq1Dgl1Dgj1DgcNDgaNDgVNDgTNDgMFDgJFDgGFDgDFjh9KHDuUOW4odcxQ7nzwU+efbhp04XzQ9LTlyOvkkNlJIbeTQXYngfxODAFOCBFOBCFOADHO/LQFciaIckYIcwaIc3oIdDqIdFoIdRqIdU4Idg6IdnYIdwTxzgYVOCtU4by+JkqcJ1TjLLci56M/xC7LP4aKprVo/E2hAAAAAElFTkSuQmCC\"/>"
        cm.setGutterMarker(this.active[1], "breakpoints", cm.lineInfo(this.active[1]).gutterMarkers ? null : marker);
      }
    },
    cleanActiveLine(cm){
      for (var i = 0; i < this.active.length; i++) {
        cm.removeLineClass(this.active[i], "wrap", "CodeMirror-activeline");
        cm.removeLineClass(this.active[i], "background", "CodeMirror-activeline-background");
        cm.removeLineClass(this.active[i], "background", "CodeMirror-red-background");
        cm.removeLineClass(this.active[i], "background", "CodeMirror-blue-background");
        cm.removeLineClass(this.active[i], "gutter", "CodeMirror-activeline-gutter");
        cm.setGutterMarker(this.active[i], "breakpoints",null)
      }
      this.active=[]
    },
    //scroll to the current line
    scrollTo(line){
      // 编辑器高
      let editorHeight = this.$refs.cm.codemirror.getWrapperElement().offsetHeight;
      // 获取当前行高
      let lineHeight = this.$refs.cm.codemirror.defaultTextHeight();
      // 移动到第line行
      let scrollY = Math.round(lineHeight*line-editorHeight/2);
      this.$refs.cm.codemirror.scrollTo(null, scrollY);
    },
    //console information
    onConsoleClick(){
      this.consoleInfoShow=!this.consoleInfoShow
      if(this.consoleInfoShow){
        //console notification
        this.consoleInfoNotification=this.$notify({
          title: 'Console Output',
          dangerouslyUseHTMLString: true,
          message: this.setConsoleHTML(this.currentStackList[0].console),
          duration: 0,
          showClose: false
        })
      }
      else{
        Notification.closeAll()
      }
    },
    setConsoleHTML(msg){
      return this.toHTML(msg)
    },
    toHTML(text){
      var html = text;
       if (html) {
          html = html.replace(/  /g, "&ensp;&ensp;");
          html = html.replace(/</g,"&lt;")
          html = html.replace(/>/g,"&gt;")
          html = html.replace(/com[\\/]cr[\\/]debuggee[\\/]user[0-9]*[\\/]/g, "");
          html = html.replace(/\n/g, "<br/>");
          html = html.replace(/.java:[0-9]*/g, (str)=>{
            var line=Number(str.split(".java:")[1])
            return ".java:"+(line-1)
          });
          html = html.replace(/api.runApi./g,"")
       }
       return html; 
    },
    getLang(mode){
      if(mode=="text/x-java"){
        return "java"
      }
      else if(mode=="python"){
        return "python"
      }
      return ""
    }
  }
}