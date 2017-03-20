var body = arguments[0];
var styleElement = document.createElement("style");
styleElement.appendChild(document.createTextNode(`
    .kix-cursor {
        display: none !important;
    }
    
    .docs-border-selection-button-normal {
        visibility: hidden !important;
    }
`));
body.appendChild(styleElement);
