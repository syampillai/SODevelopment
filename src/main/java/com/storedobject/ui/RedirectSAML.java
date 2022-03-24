package com.storedobject.ui;

import com.storedobject.common.SORuntimeException;
import com.storedobject.helper.ID;
import com.storedobject.helper.LitComponent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

/**
 * Class used to redirect the user to another site via SAML authentication.
 *
 * @author Syam
 */
@Tag("so-redirect-saml")
@JsModule("./so/saml-redirect/saml-redirect.js")
public class RedirectSAML extends LitComponent {

    private boolean saml, site;

    /**
     * Constructor.
     */
    public RedirectSAML() {
        getElement().setProperty("idSAML", "soSAML" + ID.newID());
    }

    /**
     * Set the relay state (optional).
     *
     * @param relayState Relay state to set.
     */
    public void setRelayState(String relayState) {
        getElement().setProperty("relayState", relayState);
    }

    /**
     * Set SAML response. Please make sure that you pass a valid Base64 encoded SAML response block.
     *
     * @param samlResponse SAML response to set.
     */
    public void setSAMLResponse(String samlResponse) {
        getElement().setProperty("samlResponse", samlResponse);
        this.saml = samlResponse != null && !samlResponse.isEmpty();
    }

    /**
     * Set site to which SAML response should be sent.
     *
     * @param site Site.
     */
    public void setSite(String site) {
        getElement().setProperty("site", site);
        this.site = site != null && !site.isEmpty();
    }

    /**
     * Go to the new site by submitting the SAML response to the site set via @{link #setSite(String)}. The application
     * may get redirected based on the response from the site.
     */
    public void go() {
        if(!saml) {
            throw new SORuntimeException("'SAML Response' not set");
        }
        if(!site) {
            throw new SORuntimeException("'Site' not set");
        }
        saml = false;
        executeJS("go");
    }
}
