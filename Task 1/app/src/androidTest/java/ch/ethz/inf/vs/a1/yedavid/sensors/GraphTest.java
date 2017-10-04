package ch.ethz.inf.vs.a1.nethz.sensors;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class GraphTest {

    @Rule
    public ActivityTestRule<SensorActivity> mActivityRule = new ActivityTestRule(SensorActivity.class);

    SensorActivity mActivity;
    private GraphContainer graphContainer;

    // For exception handling
    private boolean ret = false;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityRule.getActivity();
        assertNotNull(mActivity);
        graphContainer = mActivity.getGraphContainer();
        assertNotNull(graphContainer);
    }

    /*
     * Simple test. 10 values.
     */
    @Test
    public void test1() {
        float[][] values = createArray(3, 10, 1);
        carryOutTest(values, values);
    }

    /*
     * Exactly 100 values
     */
   @Test
    public void test2() {
        float[][] values = createArray(3, 100, 1);
        carryOutTest(values, values);
    }

    /*
     * 101 values. Last 100 should be retrieved
     */
    @Test
    public void test3() {
        float[][] values = createArray(3, 101, 1);
        float[][] target = createArray(3, 100, 4);
        carryOutTest(values, target);
    }

    /*
     * 200 values. Last 100 should be retrieved.
     */
    @Test
    public void test4() {
        float[][] values = createArray(3, 200, 1);
        float[][] target = createArray(3, 100, 301);
        carryOutTest(values, target);
    }

    /*
     * 1000 values. Last 100 should be retrieved.
     */
    @Test
    public void test5() {
        float[][] values = createArray(3, 1000, 1);
        float[][] target = createArray(3, 100, 2701);
        carryOutTest(values, target);
    }

    /*
     * 0 values. Should retrieve an array with a length of 0.
     */
    @Test
    public void test6() {
        float[][] values = createArray(0, 3, 1);
        carryOutTest(values, values);
    }

    /*
     * Starting with value arrays of length 3, then switching to a length of 1. There should be an exception (though not a NullPointerException).
     */
    @Test
    public void test7() {
        float[][] values = {{1.0f, 1.0f, 1.0f}, {1.0f}};
        carryOutTest(values, values);
    }

    /*
     * Starting with value arrays of length 1, then switching to a length of 3. There should be an exception. 
     */
    @Test
    public void test8() {
        float[][] values = {{1.0f}, {1.0f, 1.0f, 1.0f}};
        try {
            carryOutTest(values, values);
        } catch (NullPointerException n) {
            fail();
        } catch (Exception e) {
            // Pass
        }
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
        graphContainer = null;
    }

    private void carryOutTest(final float[][] values, float[][] target) {
        ret = false;
        for (int i = 0; i < values.length; i++) {
            try {
                graphContainer.addValues(i + 1, values[i]);
            } catch (NullPointerException n) {
                fail();
            } catch (Exception e) {
                // Pass
                ret = true;
            }
        }
        if(ret){
            return;
        }
        float[][] retrievedValues = graphContainer.getValues();
        assertEquals(target.length, retrievedValues.length);
        for (int i = 0; i < target.length; i++) {
            assertEquals(target[i].length, retrievedValues[i].length);
            for (int j = 0; j < target[0].length; j++) {
                assertEquals(target[i][j], retrievedValues[i][j]);
            }
        }
    }

    private float[][] createArray(final int dimM, final int dimN, final int startCounter) {
        int counter = startCounter;
        float[][] values = new float[dimM][];
        for (int i = 0; i < dimM; i++) {
            values[i] = new float[dimN];
            for (int j = 0; j < dimN; j++) {
                values[i][j] = (float) counter;
                counter++;
            }
        }
        return values;
    }
}