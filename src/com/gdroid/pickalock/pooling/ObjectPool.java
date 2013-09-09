package com.gdroid.pickalock.pooling;

import com.gdroid.pickalock.utils.SLog;


public abstract class ObjectPool {
    private FixedSizeArray<Object> mAvailable;
    private int mSize;

    private static final int DEFAULT_SIZE = 32;
    
    public ObjectPool() {
        super();
        setSize(DEFAULT_SIZE);
    }

    public ObjectPool(int size) {
        super();
        setSize(size);
    }
    
    /** Allocates an object from the pool */
    protected Object allocate() {
        Object result = mAvailable.removeLast();
        if(result == null) throw new IllegalStateException("Object pool of type " + this.getClass().getSimpleName() + " exhausted!");
        // how to enable assertions in android?
        assert result != null : "Object pool of type " + this.getClass().getSimpleName()
                                + " exhausted!!";
        return result;
    }

    /** Returns an object to the pool. */
    public void release(Object entry) {
        mAvailable.add(entry);
    }

    /** Returns the number of pooled elements that have been allocated but not released. */
    public int getAllocatedCount() {
        return mAvailable.getCapacity() - mAvailable.getCount();
    }
    
    private void setSize(int size) {
        mSize = size;
        mAvailable = new FixedSizeArray<Object>(mSize);

        fill();
    }

    protected abstract void fill();

    protected FixedSizeArray<Object> getAvailable() {
        return mAvailable;
    }

    protected int getSize() {
        return mSize;
    }
    
      
   
}
