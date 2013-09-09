package com.gdroid.pickalock.pooling;


public abstract class TObjectPool<T> extends ObjectPool {

    public TObjectPool() {
        super();
    }

    public TObjectPool(int size) {
        super(size);
    }
    
    @SuppressWarnings("unchecked")
    @Override
	public T allocate() {
        T object = (T)super.allocate();        
        return object;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void release(Object entry) {
    	if(entry == null)
    		return;
    	
    	clean((T) entry);
    	super.release(entry);
    }
    
    /**
     * Cleans this object to restore initial state.
     * @param o
     */
    protected abstract void clean(T t);  

}

