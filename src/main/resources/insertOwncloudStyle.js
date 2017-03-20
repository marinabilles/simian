var body = arguments[0];
var styleElement = document.createElement("style");
styleElement.appendChild(document.createTextNode(`
    editinfo {
        visibility: hidden;
    }
    
    .caret {
        visibility: hidden !important;
    }
`));
body.appendChild(styleElement);
