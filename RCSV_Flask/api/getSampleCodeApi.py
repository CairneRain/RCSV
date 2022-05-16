def getSampleCode(sample_file):
    with open(sample_file, 'r') as f:
        lines = f.readlines()
    return "".join(lines)
