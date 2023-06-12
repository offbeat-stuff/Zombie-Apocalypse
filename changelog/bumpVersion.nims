import strutils

var props = readFile("gradle.properties").splitLines()

var start = -1
for i in 0 ..< props.len():
    if props[i].find("mod_version = ") != -1:
        start = i
        break

assert start != -1

var f = props[start].split("mod_version = ")
assert f.len() == 2

func bumpVersion(f: string): string=
    var a = split(f,'.')
    assert a.len() == 3
    a[2] = $(parseInt(a[2]) + 1)
    return a.join(".")

f[1] = bumpVersion(f[1])

props[start] = f.join("mod_version = ")

writeFile("gradle.properties",props.join("\n"))