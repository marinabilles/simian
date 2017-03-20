var cb = arguments[arguments.length - 1];

function clearText() {
    firepad.setText("");
    cb();
}

if (isfirepadready) {
    clearText()
} else {
    firepad.on("ready", clearText);
}

