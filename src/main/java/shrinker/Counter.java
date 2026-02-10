/* Apave Image Shrinker - Counter
 * Rory Hafey (roryngh@hotmail.com)
 * Apave Newcastle
 * 2025
 */

package shrinker;

public class Counter {
    private int numDone = 0;

    public Counter(){}

    public synchronized void incDone(){
        numDone++;
//        System.out.println(numDone);
    }

    public synchronized int getNumDone(){
        return numDone;
    }

    public synchronized void resetNumDone(){
        numDone = 0;
    }

}

