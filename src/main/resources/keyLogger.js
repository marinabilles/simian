var newDiv = document.createElement("div");
newDiv.id = "keyLogger";
newDiv.style = "height: 50px; overflow-y: scroll";
var table = document.createElement("table");
newDiv.appendChild(table);
var tr = document.createElement("tr");
["Event", "key", "code", "keyCode", "charCode", "which", "ctrlKey", "shiftKey", "altKey"].forEach(function(v) {
    var th = document.createElement("th");
    th.appendChild(document.createTextNode(v));
    tr.appendChild(th);
});
table.appendChild(tr);
function td(t) {
    var tdEl = document.createElement("td");
    var text = document.createTextNode(t);
    tdEl.appendChild(text);
    return tdEl;
}
function append(hand, e) {
    var tr = document.createElement("tr");
    var cols = [hand, e.key, e.code, e.keyCode, e.charCode, e.which, e.ctrlKey, e.shiftKey, e.altKey];
    cols.forEach(function(t) {
        tr.appendChild(td(t));
    });
    table.appendChild(tr);
}
var body = document.getElementsByTagName("body")[0];
window.addEventListener("keydown", function(e) {
    append("keydown", e);
}, true);
window.addEventListener("keypress", function(e) {
    append("keypress", e);
}, true);
window.addEventListener("keyup", function(e) {
    append("keyup", e);
}, true);
body.insertBefore(newDiv, body.childNodes[0]);
