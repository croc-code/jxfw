package ru.croc.ctp.jxfw.core.impl;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;

public final class ResolvableTypeHelper {
    
    private ResolvableTypeHelper() {
        
    }
    
    public static ResolvableType getResolvableTypeForEvent(ApplicationEvent event) {
        return ResolvableType.forClassWithGenerics(event.getClass(), ResolvableType.forInstance(event.getSource()));
    }
}