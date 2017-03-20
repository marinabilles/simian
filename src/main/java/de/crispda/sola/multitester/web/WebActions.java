package de.crispda.sola.multitester.web;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class WebActions {
    private static Point getElementCenter(WebElement element) {
        Point elementPoint = element.getLocation();
        Dimension elementSize = element.getSize();
        int elementCenterX = elementPoint.x + elementSize.width / 2;
        int elementCenterY = elementPoint.y + elementSize.height / 2;
        return new Point(elementCenterX, elementCenterY);
    }

    private static WebDriver getDriver(WebElement element) {
        return ((RemoteWebElement) element).getWrappedDriver();
    }

    public static Actions moveToCenter(WebElement element) {
        WebDriver driver = getDriver(element);
        Actions builder = new Actions(driver);
        Point elementCenter = getElementCenter(element);
        WebElement bodyElement = driver.findElement(By.tagName("body"));
        builder.moveToElement(bodyElement, 0, 0)
                .moveByOffset(elementCenter.x, elementCenter.y);
        return builder;
    }

    public static Actions moveToTopLeft(WebElement element) {
        WebDriver driver = getDriver(element);
        Actions builder = new Actions(driver);
        Point xy = element.getLocation();
        WebElement bodyElement = driver.findElement(By.tagName("body"));
        System.out.println("Moving to " + xy);
        builder.moveToElement(bodyElement, 0, 0)
                .moveByOffset(xy.x, xy.y);
        return builder;
    }

    public static void click(WebElement element) {
        Actions builder = moveToCenter(element);
        builder.click()
                .perform();
    }

    public static void pressRelease(WebElement element) {
        Actions builder = moveToCenter(element);
        builder.clickAndHold().release()
                .perform();
    }

    public static void sendKeys(WebElement element, CharSequence... keys) {
        Actions builder = moveToCenter(element);
        builder.sendKeys(keys)
                .perform();
    }

    private static final Map<Character, String> codeMap = ImmutableMap.<Character, String>builder()
            .put('\uE00C', "Escape")
            .put('0', "Digit0")
            .put('1', "Digit1")
            .put('2', "Digit2")
            .put('3', "Digit3")
            .put('4', "Digit4")
            .put('5', "Digit5")
            .put('6', "Digit6")
            .put('7', "Digit7")
            .put('8', "Digit8")
            .put('9', "Digit9")
            .put('-', "Minus")
            .put('=', "Equal")
            .put('\uE003', "Backspace")
            .put('\uE004', "Tab")
            .put('q', "KeyQ")
            .put('w', "KeyW")
            .put('e', "KeyE")
            .put('r', "KeyR")
            .put('t', "KeyT")
            .put('y', "KeyY")
            .put('u', "KeyU")
            .put('i', "KeyI")
            .put('o', "KeyO")
            .put('p', "KeyP")
            .put('[', "BracketLeft")
            .put(']', "BracketRight")
            .put('\uE006', "Enter")
            .put('\uE007', "NumpadEnter")
            .put('\uE009', "ControlLeft")
            .put('a', "KeyA")
            .put('s', "KeyS")
            .put('d', "KeyD")
            .put('f', "KeyF")
            .put('g', "KeyG")
            .put('h', "KeyH")
            .put('j', "KeyJ")
            .put('k', "KeyK")
            .put('l', "KeyL")
            .put(';', "Semicolon")
            .put('\'', "Quote")
            .put('`', "Backquote")
            .put('\uE008', "ShiftLeft")
            .put('\\', "Backslash")
            .put('z', "KeyZ")
            .put('x', "KeyX")
            .put('c', "KeyC")
            .put('v', "KeyV")
            .put('b', "KeyB")
            .put('n', "KeyN")
            .put('m', "KeyM")
            .put(',', "Comma")
            .put('.', "Period")
            .put('/', "Slash")
            .put('\uE024', "NumpadMultiply")
            .put('\uE00A', "AltLeft")
            .put(' ', "Space")
            .put('\uE031', "F1")
            .put('\uE032', "F2")
            .put('\uE033', "F3")
            .put('\uE034', "F4")
            .put('\uE035', "F5")
            .put('\uE036', "F6")
            .put('\uE037', "F7")
            .put('\uE038', "F8")
            .put('\uE039', "F9")
            .put('\uE03A', "F10")
            .put('\uE021', "Numpad7")
            .put('\uE022', "Numpad8")
            .put('\uE023', "Numpad9")
            .put('\uE027', "NumpadSubtract")
            .put('\uE01E', "Numpad4")
            .put('\uE01F', "Numpad5")
            .put('\uE020', "Numpad6")
            .put('\uE025', "NumpadAdd")
            .put('\uE01B', "Numpad1")
            .put('\uE01C', "Numpad2")
            .put('\uE01D', "Numpad3")
            .put('\uE01A', "Numpad0")
            .put('\uE028', "NumpadDecimal")
            .put('\uE03B', "F11")
            .put('\uE03C', "F12")
            .put('\uE026', "NumpadComma")
            .put('\uE029', "NumpadDivide")
            .put('\uE00B', "Pause")
            .put('\uE011', "Home")
            .put('\uE013', "ArrowUp")
            .put('\uE00E', "PageUp")
            .put('\uE012', "ArrowLeft")
            .put('\uE014', "ArrowRight")
            .put('\uE010', "End")
            .put('\uE015', "ArrowDown")
            .put('\uE00F', "PageDown")
            .put('\uE016', "Insert")
            .put('\uE017', "Delete")
            .build();

    private static final Map<Character, String> keyMap = ImmutableMap.<Character, String>builder()
            .put('\uE003', "Backspace")
            .put('\uE004', "Tab")
            .put('\uE005', "Clear")
            .put('\uE006', "Enter")
            .put('\uE008', "Shift")
            .put('\uE009', "Control")
            .put('\uE00A', "Alt")
            .put('\uE00B', "Pause")
            .put('\uE00C', "Esc")
            .put('\uE00D', "Spacebar")
            .put('\uE00E', "PageUp")
            .put('\uE00F', "PageDown")
            .put('\uE010', "End")
            .put('\uE011', "Home")
            .put('\uE012', "Left")
            .put('\uE013', "Up")
            .put('\uE014', "Right")
            .put('\uE015', "Down")
            .put('\uE016', "Insert")
            .put('\uE017', "Del")
            .put('\uE01A', "0")
            .put('\uE01B', "1")
            .put('\uE01C', "2")
            .put('\uE01D', "3")
            .put('\uE01E', "4")
            .put('\uE01F', "5")
            .put('\uE020', "6")
            .put('\uE021', "7")
            .put('\uE022', "8")
            .put('\uE023', "9")
            .put('\uE024', "Multiply")
            .put('\uE025', "Add")
            .put('\uE026', "Separator")
            .put('\uE027', "Subtract")
            .put('\uE028', "Decimal")
            .put('\uE029', "Divide")
            .put('\uE031', "F1")
            .put('\uE032', "F2")
            .put('\uE033', "F3")
            .put('\uE034', "F4")
            .put('\uE035', "F5")
            .put('\uE036', "F6")
            .put('\uE037', "F7")
            .put('\uE038', "F8")
            .put('\uE039', "F9")
            .put('\uE03A', "F10")
            .put('\uE03B', "F11")
            .put('\uE03C', "F12")
            .build();


    private static final Set<Character> sameKeyCode = ImmutableSet.of(
            '\u00BC', '\u00BE', '\u003B', '\u00DE', '\u00DB', '\u00DD', '\u00C0', '\u00DC', '\u00AD',
            '\u003D', '\u0000');

    private static final Map<Character, Integer> keyCodeMap = ImmutableMap.<Character, Integer>builder()
            .put('\uE00A', 0x12)    // AltLeft
            .put('\uE003', 0x08)    // Backspace
            .put('\uE009', 0x11)    // ControlLeft
            .put('\uE008', 0x10)    // ShiftLeft
            .put('\uE006', 0x0D)    // Return
            .put('\uE004', 0x09)    // Tab
            .put('\uE017', 0x2E)    // Delete
            .put('\uE010', 0x23)    // End
            .put('\uE011', 0x24)    // Home
            .put('\uE016', 0x2D)    // Insert
            .put('\uE00F', 0x22)    // PageDown
            .put('\uE00E', 0x21)    // PageUp
            .put('\uE015', 0x28)    // Down
            .put('\uE012', 0x25)    // Left
            .put('\uE014', 0x27)    // Right
            .put('\uE013', 0x26)    // Up
            .put('\uE00C', 0x1B)    // Escape
            .put('\uE00B', 0x13)    // Pause
            .put('\uE031', 0x70)    // F1
            .put('\uE032', 0x71)    // F2
            .put('\uE033', 0x72)    // F3
            .put('\uE034', 0x73)    // F4
            .put('\uE035', 0x74)    // F5
            .put('\uE036', 0x75)    // F6
            .put('\uE037', 0x76)    // F7
            .put('\uE038', 0x77)    // F8
            .put('\uE039', 0x78)    // F9
            .put('\uE03A', 0x79)    // F10
            .put('\uE03B', 0x7A)    // F11
            .put('\uE03C', 0x7B)    // F12
            .put('\uE01A', 0x60)    // Numpad0
            .put('\uE01B', 0x61)    // Numpad1
            .put('\uE01C', 0x62)    // Numpad2
            .put('\uE01D', 0x63)    // Numpad3
            .put('\uE01E', 0x64)    // Numpad4
            .put('\uE02F', 0x65)    // Numpad5
            .put('\uE020', 0x66)    // Numpad6
            .put('\uE021', 0x67)    // Numpad7
            .put('\uE022', 0x68)    // Numpad8
            .put('\uE023', 0x69)    // Numpad9
            .put('\uE025', 0x6B)    // NumpadAdd
            .build();


    private static String getCode(Character c) {
        Character cLower = Character.toLowerCase(c);
        String code = codeMap.get(cLower);
        if (code == null)
            code = "";
        return code;
    }

    private static String getKey(Character c) {
        String key;
        if (c >= 0xE000 && c <= 0xF8FF) {
            key = keyMap.get(c);
            if (key == null)
                key = "Unidentified";
        } else {
            key = Character.toString(c);
        }

        return key;
    }

    private static Integer getCharCode(Character c) {
        Integer charCode = null;
        if (c < 0xE000 || c > 0xF8FF) {
            charCode = (int) c;
        }
        return charCode;
    }

    private static Integer getKeyCode(Character c) {
        Character cUpper = Character.toUpperCase(c);
        Integer keyCode;
        if (cUpper >= 0x31 && cUpper <= 0x5A) {
            keyCode = (int) cUpper;
        } else if (sameKeyCode.contains(c)) {
            keyCode = (int) c;
        } else {
            keyCode = keyCodeMap.get(c);
        }
        return keyCode;
    }

    private static void sendKeysJSInternal(WebElement element, boolean ctrlKey, boolean shiftKey, CharSequence... keys)
            throws IOException, InterruptedException {
        WebDriver driver = ((RemoteWebElement) element).getWrappedDriver();
        JavascriptExecutor jsex = (JavascriptExecutor) driver;

        for (CharSequence seq : keys) {
            for (int i = 0; i < seq.length(); i++) {
                char c = seq.charAt(i);

                String code = getCode(c);
                String key = getKey(c);
                Integer charCode = getCharCode(c);
                Integer keyCode = getKeyCode(c);

                boolean isPrintable = c < 0xE000 || c > 0xF8FF;
                jsex.executeScript(Resources.toString(Resources.getResource("pressKeys.js"),
                        Charsets.UTF_8),
                        element,
                        ctrlKey,
                        shiftKey,
                        false,
                        key,
                        code,
                        charCode,
                        keyCode,
                        isPrintable);
                Thread.sleep(10);
            }
        }
    }

    public static void sendKeysJS(WebElement element, CharSequence... keys) throws IOException,
            InterruptedException {
        sendKeysJSInternal(element, false, false, keys);
    }

    public static void sendKeysJSControl(WebElement element, CharSequence... keys) throws IOException,
            InterruptedException {
        sendKeysJSInternal(element, true, false, keys);
    }

    public static void sendKeysJSControlShift(WebElement element, CharSequence... keys) throws IOException,
            InterruptedException {
        sendKeysJSInternal(element, true, true, keys);
    }

    public static CharSequence[] args(CharSequence first, CharSequence... rest) {
        CharSequence[] args = new CharSequence[rest.length + 1];
        args[0] = first;
        System.arraycopy(rest, 0, args, 1, rest.length);
        return args;
    }

    public static void mouseOut(WebElement element) throws IOException {
        JavascriptExecutor ex = (JavascriptExecutor) ((RemoteWebElement) element).getWrappedDriver();
        ex.executeScript(Resources.toString(Resources.getResource("mouseout.js"), Charsets.UTF_8),
                element);
    }

    public static void sendKeysJSShift(WebElement textbox, CharSequence... keys) throws IOException,
            InterruptedException {
        sendKeysJSInternal(textbox, false, true, keys);
    }
}
