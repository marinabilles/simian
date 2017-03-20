var element = arguments[0];

var mouseevent = new MouseEvent("mouseout", {});

element.dispatchEvent(mouseevent);
