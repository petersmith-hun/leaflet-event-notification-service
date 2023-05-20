package hu.psprog.leaflet.lens.core.observer;

import io.reactivex.rxjava3.core.Observable;

/**
 * Provides observer logic to an observable.
 *
 * @author Peter Smith
 */
public interface ObserverHandler<T> {

    /**
     * Attaches an observer to the given observable.
     *
     * @param observable an observable to attach observer to
     */
    void attachObserver(Observable<T> observable);
}
