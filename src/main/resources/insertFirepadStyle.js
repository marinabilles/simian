var body = arguments[0];
var styleElement = document.createElement("style");
styleElement.appendChild(document.createTextNode(`
    .CodeMirror-cursors {
        visibility: hidden !important;
    }
    
    .other-client {
        visibility: hidden !important;
    }
`));
body.appendChild(styleElement);
