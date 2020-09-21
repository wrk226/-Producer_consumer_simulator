/*
 * ProducerConsumer.java
 *
 * Version:
 *     1.00
 *
 */


/**
 * This code can simulate a production model. The first producer produces
 * something what the second consumer-producer needs to produce what the last
 * consumer needs. The producer of the first thread creates a d case, which
 * is used by the consumer-producer to create  a inlay and adds it to the
 * case. The consumer uses the cd with the inlay and adds a cd and creates a
 * saleable wrapped cd.
 *
 * @author: Renke Wang
 */

public class ProducerConsumer {
    static int itemType = 0;
    static int counter;

    /**
     * main function
     */
    public static void main( String[] args ) {
        //number of product
        counter = args.length == 1 ? Integer.parseInt( args[0] ) : 10;

        //create all threads and run
        Producer aProducer = new Producer();
        CP aCP = new CP();
        Consumer aConsumer = new Consumer();
        aProducer.start();
        aCP.start();
        aConsumer.start();
    }
}

/**
 * Producer class
 */
class Producer extends Thread {
    int counter = 1;

    /**
     * Producer's run function
     */
    @Override
    public void run() {
        while ( true ) {
            synchronized ( Consumer.class ) {
                synchronized ( Producer.class ) {

                    //update number of product and exit if enough
                    if ( --ProducerConsumer.counter < 0 ) {
                        System.exit( 0 );
                    }

                    //verify if skip happened
                    if ( ProducerConsumer.itemType != 0 ) {
                        System.out.println( "skipped" );
                        System.exit( 1 );
                    }
                    System.out.println( "cycle: " + counter++ );
                    System.out.println( "\t1 cd case" );

                    //notify thread wait on Producer, which is the CP thread
                    Producer.class.notifyAll();

                    //update item type
                    ProducerConsumer.itemType = 1;
                }
                try {

                    //wait on Consumer
                    Consumer.class.wait();
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}

/**
 * CP class
 */
class CP extends Thread {

    /**
     * CP's run function
     */
    @Override
    public void run() {

        //make sure the thread will run in order for the first round.
        //if id and item type doesn't match, wait
        synchronized ( Producer.class ) {
            while ( ProducerConsumer.itemType != 1 ) {
                try {
                    Producer.class.wait();
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
        while ( true ) {
            synchronized ( Producer.class ) {
                synchronized ( CP.class ) {

                    //verify if skip happened
                    if ( ProducerConsumer.itemType != 1 ) {
                        System.out.println( "skipped" );
                        System.exit( 1 );
                    }
                    System.out.println( "\t2 add inlay" );

                    //notify thread wait on CP, which is the Consumer thread
                    CP.class.notifyAll();

                    //update item type
                    ProducerConsumer.itemType = 2;
                }
                try {

                    //wait on Producer
                    Producer.class.wait();
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}

/**
 * Consumer class
 */
class Consumer extends Thread {

    /**
     * Consumer's run function
     */
    @Override
    public void run() {

        //make sure the thread will run in order for the first round.
        //if id and item type doesn't match, wait
        synchronized ( CP.class ) {
            while ( ProducerConsumer.itemType != 2 ) {
                try {
                    CP.class.wait();
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
        while ( true ) {
            synchronized ( CP.class ) {
                synchronized ( Consumer.class ) {

                    //verify if skip happened
                    if ( ProducerConsumer.itemType != 2 ) {
                        System.out.println( "skipped" );
                        System.exit( 1 );
                    }
                    System.out.println( "\t3 wrapped cd" );

                    //notify thread wait on CP, which is the Producer thread
                    Consumer.class.notifyAll();

                    //update item type
                    ProducerConsumer.itemType = 0;
                }
                try {

                    //wait on CP
                    CP.class.wait();
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}