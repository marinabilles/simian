var body = document.getElementsByTagName("body").item(0);

var childNodes = body.childNodes;

var output = "";
for (var i = 0; i < childNodes.length; i++) {
    var node = childNodes.item(i);
    output += "<" + node.tagName;
    var attrs = node.attributes;
    for (var j = 0; j < attrs.length; j++) {
        var attr = attrs.item(j);
        output += " " + attr.name + "=" + attr.value;
    }
    output += ">\n";
}

return output;
