import osproc
import strutils,sequtils

var commits = execCmdEx("git log --oneline --no-decorate").output.splitLines()
    .mapIt(it.substr(8))

commits.setLen(commits.find("bump version"))

func checkMessage(x,y: string): bool= x.toLower().startsWith(y)

proc generateList(x: string): seq[string]=
    commits.filterIt(checkMessage(it,x & ": ")).mapIt(it.substr(x.len() + 2))

proc printList(x,y: string)=
    let list = generateList(x)
    if list.len() == 0:
        return
    echo "## ",y
    echo ""
    for i in list:
        echo "- ",i
    echo ""
    echo ""

printList("feat","New Features")
printList("change","Changes")
printList("fix","Fixes")