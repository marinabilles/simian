if (typeof arguments[1] !== "boolean")
    throw "ctrlKey not boolean";
if (typeof arguments[2] !== "boolean")
    throw "shiftKey not boolean";
if (typeof arguments[3] !== "boolean")
    throw "altKey not boolean";

var pressKeyCode = (arguments[8] ? 0 : arguments[7]);

// var keyLogger = document.getElementById("output");
// keyLogger.appendChild(document.createTextNode(
//    `arguments[6]: ${arguments[6]}, arguments[7]: ${arguments[7]}, pressKeyCode: ${pressKeyCode}`));
// keyLogger.appendChild(document.createElement("br"));

var pressData = {
    bubbles: true,
    cancelable: true,
    view: window,
    ctrlKey: arguments[1],
    shiftKey: arguments[2],
    altKey: arguments[3],
    key: arguments[4],
    code: arguments[5],
    charCode: arguments[6],
    keyCode: pressKeyCode,
    which: arguments[6]
};

var downUpData = {
    bubbles: true,
    cancelable: true,
    view: window,
    ctrlKey: arguments[1],
    shiftKey: arguments[2],
    altKey: arguments[3],
    key: arguments[4],
    code: arguments[5],
    charCode: 0,
    keyCode: arguments[7],
    which: arguments[7]
};

var keydownEvent = new KeyboardEvent("keydown", downUpData);
var keypressEvent = new KeyboardEvent("keypress", pressData);
var keyupEvent = new KeyboardEvent("keyup", downUpData);

var element = arguments[0];

var ctrlData;
var ctrlKey = arguments[1];
if (ctrlKey) {
    ctrlData = {
        bubbles: true,
        cancelable: true,
        view: window,
        ctrlKey: true,
        shiftKey: false,
        altKey: false,
        key: "Control",
        code: "ControlLeft",
        charCode: 0,
        keyCode: 17,
        which: 17
    };

    element.dispatchEvent(new KeyboardEvent("keydown", ctrlData));
}

var shiftData;
var shiftKey = arguments[2];
if (shiftKey) {
    shiftData = {
        bubbles: true,
        cancelable: true,
        view: window,
        ctrlKey: true,
        shiftKey: true,
        altKey: false,
        key: "Shift",
        code: "ShiftLeft",
        charCode: 0,
        keyCode: 0x10,
        which: 0x10
    };

    element.dispatchEvent(new KeyboardEvent("keydown", shiftData));
}

window.setTimeout(function() {
    element.dispatchEvent(keydownEvent);
    element.dispatchEvent(keypressEvent);
    window.setTimeout(function() {
        element.dispatchEvent(keyupEvent);
        if (ctrlKey) {
            element.dispatchEvent(new KeyboardEvent("keyup", ctrlData));
        }
        if (shiftKey) {
            element.dispatchEvent(new KeyboardEvent("keyup", shiftData));
        }
    }, 10)
}, 0);
