package ru.practicum.shareit.booking.stratagy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.practicum.shareit.booking.model.State;

@Component
public class BookingStateFetchStrategyFactory {

    private Map<State, BookingStateFetchStrategy> strategies;

    @Autowired
    public BookingStateFetchStrategyFactory(Set<BookingStateFetchStrategy> strategySet) {
        createStrategy(strategySet);
    }

    public BookingStateFetchStrategy findStrategy(State state) {
        return strategies.get(state);
    }

    private void createStrategy(Set<BookingStateFetchStrategy> strategySet) {
        strategies = new HashMap<>();
        strategySet.forEach(
                strategy -> strategies.put(strategy.getStrategyState(), strategy));
    }
}
