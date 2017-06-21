import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.io.File;
import java.io.FileInputStream;
import java.util.Comparator;
import java.util.PriorityQueue;

public class ALSSchedulerTest {
    private ALSScheduler alsScheduler;

    @Before
    public void before() throws Exception {
        alsScheduler = new ALSScheduler();
        System.out.println("#####Test Begins#####");
    }

    @After
    public void after() throws Exception {
        alsScheduler = null;
        System.out.println("#####Test Ends#####");
    }


    /**
     * Method:repOK()
     */
    @Test
    public void testRepOK() throws Exception {
        assert alsScheduler.repOK();
        alsScheduler.decPriQue = null;
        assert !alsScheduler.repOK();
        alsScheduler.incPriQue = null;
        assert !alsScheduler.repOK();
        alsScheduler.first = 2;
        assert !alsScheduler.repOK();
    }

    /**
     * Method: compute()
     */
    @Test
    public void testCompute() throws Exception {
        alsScheduler.checkFloor("FR","1","UP","0");
        alsScheduler.checkFloor("FR","1","UP","0");
        alsScheduler.checkElevator("ER","5","0");
        alsScheduler.checkElevator("ER","5","0");
        alsScheduler.checkElevator("ER","5","4"); 
        alsScheduler.checkElevator("ER","1","1");
        alsScheduler.checkElevator("ER","2","1");
        alsScheduler.checkFloor("FR","6","UP","1");
        alsScheduler.checkFloor("FR","7","UP","2");
        alsScheduler.checkFloor("FR","7","UP","2");
        alsScheduler.checkFloor("FR","9","UP","2");
        alsScheduler.checkFloor("FR","8","DOWN","2");
        alsScheduler.checkFloor("FR","1","UP","20");
        alsScheduler.checkElevator("ER","8","21");
        alsScheduler.checkFloor("FR","9","UP","23");
        alsScheduler.checkElevator("ER","10","23");
        alsScheduler.checkElevator("ER","9","23");
        alsScheduler.checkElevator("ER","10","100");
        alsScheduler.checkElevator("ER","9","101");
        alsScheduler.checkElevator("ER","8","101");
        alsScheduler.checkFloor("FR","8","DOWN","101");
        alsScheduler.checkElevator("ER","1","101");
        alsScheduler.checkElevator("ER","10","110");
        alsScheduler.checkElevator("ER","10","116");
        alsScheduler.checkElevator("ER","10","117");
        alsScheduler.compute();
        alsScheduler.que.que.clear();
        Request request = new Request("FR", 1, 1,0);
        alsScheduler.que.addReq(request);
        alsScheduler.upDown = 1;
        request = new Request("FR", 2, 1,0);
        request.setInvalidOut(1);
        alsScheduler.que.addReq(request);
        alsScheduler.compute();
        request = new Request("ER", 3, 1);
        request.setInvalidOut(1);
        alsScheduler.que.addReq(request);
        alsScheduler.compute();
        try {
            System.setIn(new FileInputStream(new File("test.txt")));
        } catch (Exception e) {

        }
        ElevatorSchedule elevatorSchedule = new ElevatorSchedule();
        String[] args = null;
        elevatorSchedule.main(args);
    }


    /**
     * Method: checkBulb(Request r1, Request r2)
     */
    @Test
    public void testCheckBulb() throws Exception {
        assert !alsScheduler.checkBulb(null,null);
        assert !alsScheduler.checkBulb(null,new Request("FR",3,1,0));
        assert !alsScheduler.checkBulb(new Request("FR",3,1,0),null);
        Request r1 = new Request("FR",3,1,0);
        Request r2 = new Request("FR",3,1,0);
        assert alsScheduler.checkBulb(r1,r2);
        r1 = new Request("ER",3,0);
        r2 = new Request("ER",3,1);
        assert alsScheduler.checkBulb(r1,r2);
        r1 = new Request("FR",9,0,1);
        assert !alsScheduler.checkBulb(r1,r2);
        r2 = new Request("FR", 9, 1, 1);
        assert !alsScheduler.checkBulb(r1,r2);
        r2 = new Request("FR", 8, 0, 1);
        assert !alsScheduler.checkBulb(r1,r2);
        r1 = new Request("ER", 1, 1);
        r2 = new Request("ER", 0, 1);
        assert !alsScheduler.checkBulb(r1, r2);
    }


    /**
    *
    * Method: checkPickUp(Request r, PriorityQueue<Request> q, int upDown)
    *
    */
    @Test
    public void testCheckPickUp() throws Exception {
        PriorityQueue inc = new PriorityQueue<>(new Comparator<Request>() {//递增优先队列
            @Override
            public int compare(Request o1, Request o2) {
                if (o1.getFloor()!=o2.getFloor()) return o1.getFloor()-o2.getFloor();
                else return o1.getOrder()-o2.getOrder();
            }
        });
        PriorityQueue dec = new PriorityQueue<>(new Comparator<Request>() {//递降优先队列
            @Override
            public int compare(Request o1, Request o2) {
                if (o1.getFloor()!=o2.getFloor()) return o2.getFloor()-o1.getFloor();
                else return o1.getOrder()-o2.getOrder(); 
            }
        });
        dec.offer(new Request("ER", 4, 0));
        assert !(alsScheduler.checkPickUp(new Request("ER",4,0),dec,1));
        dec.clear();
        Request request = new Request("ER",4,0);
        request.setAlreadyOut(1);
        alsScheduler.elv.update(request); 
        assert (alsScheduler.checkPickUp(new Request("ER",3,0),dec,0));
        inc.offer(new Request("FR",1,1,0)); 
        assert !(alsScheduler.checkPickUp(new Request("FR",1,1,0),inc,0));
        assert !(alsScheduler.checkPickUp(new Request("FR",1,1,0),inc,-1));
        assert !(alsScheduler.checkPickUp(new Request("FR",4,1,0),inc,1));
        dec.offer(new Request("FR",10,1,0));
        assert !(alsScheduler.checkPickUp(new Request("FR",4,1,0),dec,-1));
        assert !(alsScheduler.checkPickUp(new Request("ER",4,0),dec,-1));
        dec.offer(new Request("FR",5,1,0)); 
        assert !alsScheduler.checkPickUp(new Request("ER", 4, 1), dec, 1);
        assert !alsScheduler.checkPickUp(new Request("FR", 4, 0, 1), dec, 1);        
        assert !alsScheduler.checkPickUp(new Request("FR", 4, 0, 100), dec, 1); 
        assert !alsScheduler.checkPickUp(new Request("FR", 4, 0, 100), dec, -1); 
        assert !alsScheduler.checkPickUp(new Request("FR", 10, 0, 100), dec, -1); 
        
    }
}
