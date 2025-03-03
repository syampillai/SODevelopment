package com.storedobject.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;

import jakarta.servlet.http.HttpServletResponse;

@Tag("div")
public final class ErrorEater extends Component implements HasErrorParameter<Exception> {

    @Override
    public int setErrorParameter(BeforeEnterEvent beforeEnterEvent, ErrorParameter<Exception> errorParameter) {
        try {
            Application a = Application.get();
            if(a != null) {
                a.log(errorParameter.getException());
            }
        } catch(Throwable ignored) {
        }
        Span message = new Span("Service is not available now, please contact support!");
        getElement().appendChild(message.getElement());
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
