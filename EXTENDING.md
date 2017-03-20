# Extending Simian

Adapting Simian to a new application is a non-trivial task, because of the slightly different Selenium commands that need to be implemented to perform various tasks in the Simian algorithm, such as setting up a new document, performing individual actions on the editor, as well as reading and modifying the cursor position of the editor.

Most collaborative editors are implemented using non-standard input technologies instead of default HTML text input elements like `<textarea>`. For example, Google Docs provides a hidden iframe in which keystrokes are received by a special DOM element. These architectural oddities mean that standard Selenium APIs like `WebElement.click` and `WebElement.sendKeys` are unlikely to work with the average collaborative web application. Instead, workarounds commonly have to be used, such as generating keystrokes via injected JavaScript. This means that adapting Simian to a new application incurs significant development time, just for getting tasks such as "type a text" and "select a text and click on a button" work correctly in the target application. We have provided the `WebActions` class with the static methods `click`, `sendKeys` and `sendKeysJS` (for the JavaScript-generated keystrokes variant) to ease development of Selenium actions.

Adapting Simian to a new application requires the instantiation of a new `ExperimentSpec`, as shown in the class `ExperimentSpecs`.

```java
new SetExperimentSpec(
        Sets.newHashSet(
                new MyAction(),
                // ...
        ),
        new MyCombinator(),
        new Rectangle(0, 0, 960, 111),
        new MyInit(),
        new MyCursorSender(),
        new MyCursorReceiver(),
        "MyApp AS5"
)
```

The constructor for `SetExperimentSpec` takes the following arguments:

* `Set<Interaction>` -- a set of implemented actions such as "write the letter A" and "select the text before the previous cursor position and make it bold".

    ```java
class MyAction implements Interaction {
    private transient WebDriver driver;
    void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    void perform() throws IOException, InterruptedException {
        /* write the text "a" into a given
         * text input element in the DOM   */
        WebElement textinput = driver.findElement(By.id("textinput"));
        WebActions.sendKeys(textinput, "a");
    }
}
    ```

* `Combinator` -- a class implementing the following methods:
    * `combine(List<Interaction>)` -- this method returns a `CombinedTest` object, whose `test` method executes the provided interactions sequentially, equivalent to a single thread's part of the concurrent execution phase of Simian.

    ```java
    class MyCombinator implements Combinator {
        @Override
        public CombinedTest combine(List<Interaction> actions) {
            return combine(actions, 7000);
        }

        @Override
        public CombinedTest combineSequential(List<Interaction> actions) {
            return combine(actions, 500);
        }

        private CombinedTest combine(List<Interaction> actions,
                                     long sleepDuration) {
            final int maybeWaitCount = actions.size() + 1;
            return new CombinedTest() {
                @Override
                public List<Interaction> getInteractions() {
                    return actions;
                }

                @Override
                public void test() throws Exception {
                    // perform possible login step for the application here
                    // ...
                    MyApp.injectCSS(driver);
                    maybeWait();
                    for (Interaction action : actions) {
                        action.setDriver(driver);
                        try {
                            action.perform();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Thread.sleep(sleepDuration);
                        maybeWait();
                    }
                }

                @Override
                public int getMaybeWaitCount() {
                    return maybeWaitCount;
                }

                @Override
                public String getInitialURL() {
                    return "http://myurl...";
                }
            }
        }
    }
    ```

    The method `maybeWait` must be called before the first action and after each action performed -- it is a synchronization point between two threads each running its own `CombinedTest`.

    The `getInitialURL` method should return the startup URL of the application's document. In the case that your application requires a login step, you can perform it in the `test` method before the first action. In the case that your application has a visible cursor caret that appears in screenshots and interferes with the screenshot differencing algorithm, you can include a custom CSS stylesheet into your page in the login step of the `test` method.

    ```java
    class MyApp {
        static void injectCSS(WebDriver driver) {
            ((JavascriptExecutor) driver).executeScript(Resources.toString(
                        Resources.getResource("insertMyAppCSS.js"),
                        Charsets.UTF_8));
        }
    }
    ```
    ```javascript
    // insertMyAppCSS.js
    var body = document.getElementsByTagName("body").item(0);
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
    ```

    There should be a large delay between the interactions, large enough to allow for potential synchronization actions of the other thread

    * `combineSequential(List<Interaction>)` -- This method is used for the sequential exploring phase of Simian. It is equivalent to `combine`, except that the delay between actions does not need to take synchronization into account.

* `List<Rectangle>` -- a list of rectangles of screen coordinates in a 960 * 900 Firefox window that should be ignored for screenshot comparison purposes. This should include the non-content user interface of your application, such as toolbars and chat windows. For example, for Google Docs, we exclude `Rectangle(0, 0, 960, 111)`.

* `TestInit` -- an initialization script that makes sure the document that has been opened is empty. This is called on one of the two clients before each sequential or parallel test.

    ```java
class MyInit extends TestInit {
    @Override
    protected void init() throws InterruptedException {
        // perform login step
        // ...
        WebElement textinput = driver.findElement(By.id("textinput"));
        textinput.clear();
    }
}
    ```

* `sender` and `receiver` used to adjust the clients' cursor positions after the sequential prefix of a multi-client interaction.

    * `sender` -- reads the current cursor position.
    * `receiver` -- given a new cursor position, updates the current cursor position to the new one.    


```java
class MyCursorSender implements Exchanges {
    private transient Exchanger<Point> exchanger;

    @Override
    public void setExchanger(Exchanger<Point> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        // get cursor point
        Point cursorPoint = // ...
        exchanger.exchange(cursorPoint);
    }
}

class MyCursorReceiver implements Exchanges {
    private transient Exchanger<Point> exchanger;

    @Override
    public void setExchanger(Exchanger<Point> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void perform() throws IOException, InterruptedException {
        Point cursorPoint = exchanger.exchange(null);
        // set cursor point
        //...
    }
}
```

* `name` -- the String used in the GUI to describe the specification.

If you put your new `SetExperimentSpec` in the list created statically in [`ExperimentSpecs.java`](src/main/java/de/crispda/sola/multitester/runner/ExperimentSpecs.java), it will appear for selection in Simian's GUI.
