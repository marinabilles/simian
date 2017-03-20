package de.crispda.sola.multitester;

import com.google.common.collect.Lists;
import de.crispda.sola.multitester.scenario.*;

import java.util.List;

public class ScenarioSingleTests extends TestFixture {

    @org.testng.annotations.Test
    public void gDocsWriteSingle() {
        List<Test> tests = Lists.newArrayList(new GDocsTestWrite());
        singleThreadedTest(tests);
    }

    @org.testng.annotations.Test
    public void gDocsDeleteSingle() {
        List<Test> tests = Lists.newArrayList(new GDocsTestSelectAndDelete());
        singleThreadedTest(tests);
    }

    @org.testng.annotations.Test
    public void gDocsInsertTable() {
        List<Test> tests = Lists.newArrayList(GDocsInteractions.create(
                Lists.newArrayList(new GDocsInteractionInsertTable(), new GDocsInteractionTableAddRow())));
        setInit(new GDocsInitLines());
        singleThreadedTest(tests);
    }
}
