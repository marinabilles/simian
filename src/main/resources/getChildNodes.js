var outputArray = [];
var childNodes = arguments[0].childNodes;
var i;
for (i = 0; i < childNodes.length; i++) {
    if (childNodes[i] instanceof Element) {
        outputArray.push(childNodes[i]);
    } else {
        outputArray.push(childNodes[i].data);
    }
}
return outputArray;
