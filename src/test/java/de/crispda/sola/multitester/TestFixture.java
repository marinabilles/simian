package de.crispda.sola.multitester;

import de.crispda.sola.multitester.web.DriverSupplier;
import de.crispda.sola.multitester.web.Drivers;
import de.crispda.sola.multitester.web.Firefox;
import org.testng.annotations.BeforeClass;

import java.util.List;
import java.util.Set;

public abstract class TestFixture {
    private DriverSupplier driverSupplier;
    private TestInit init;

    @BeforeClass
    public void setup() {
        driverSupplier = Drivers.firefoxDriver(Firefox.ESR);
    }

    protected void setInit(TestInit init) {
        this.init = init;
    }

    protected void simpleScheduleTest(List<Test> scenarios) {
        MultiTestGenerator simpleGen = new SimpleMultiTestGenerator();
        simpleGen.putAll(scenarios);
        Set<MultiTest<Test>> tests = simpleGen.generateTests();
        VariantCreator variantCreator = new SimpleVariantCreator();
        variantCreator.putAll(tests);
        List<ScheduleVariant<?>> variants = variantCreator.createVariants();
        ScheduleVariantExecutor ex = new ScheduleVariantExecutor(driverSupplier);
        if (init != null) {
            ex.setInit(init);
        }
        ex.scheduleAll(variants);
        ex.execute();
    }

    protected void singleThreadedTest(List<Test> tests) {
        SingleThreadedExecutor ex = new SingleThreadedExecutor(driverSupplier);
        if (init != null)
            ex.setInit(init);
        ex.scheduleAll(tests);
        ex.execute();
    }
}
