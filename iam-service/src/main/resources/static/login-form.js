
var login_request = null;
var authorization_code_response = null;
var scopes_selected = [];
var tokens = null;
var scope_input_elements = [];

function onPageLoad() {
    console.log("on page load ...");
    login_header_element = document.getElementById("login-header");
    text_node = document.createTextNode(project_id);
    login_header_element.appendChild(text_node);
};

function splitScopesToArray(scope_string) {
    scope_string = scope_string.trim();
    if (scope_string === "") {
       return [];
    } else {
       return scope_string.split(" ");
    }
};

function submitLogin() {
    console.log("submit-login ...");
    var xhttp = new XMLHttpRequest();
    var username_value = document.getElementById("username-input").value;
    var password_value = document.getElementById("password-input").value;
    login_request = {
       "username": username_value,
       "password": password_value,
       "clientId": client_id,
       "scopes": splitScopesToArray(scope),
       "state": state,
       "redirectUri": redirect_uri
    };
    xhttp.onreadystatechange = function() {
       if (this.readyState == 4 && this.status == 200) {
          authorization_code_response = JSON.parse(xhttp.response);
          onLoginOk();
       } else if (this.readyState == 4 && this.status != 200) {
          console.log("login failed: " + this.status);
          onLoginFailed();
       }
    }
    xhttp.open("POST", getBaseUrl() + "/authorize", true);
    xhttp.setRequestHeader("content-type", "application/json");
    xhttp.send(JSON.stringify(login_request));
};

function submitConsent() {
    console.log("submit-consent ...");
    var xhttp = new XMLHttpRequest();
    consent_request = {
           "code": authorization_code_response.code,
           "scopes": scopes_selected
    };
    xhttp.onreadystatechange = function() {
       if (this.readyState == 4 && this.status == 200) {
          onConsentOk();
       } else if (this.readyState == 4 && this.status != 200) {
          console.log("login failed: " + this.status);
          onConsentFailed();
       }
    }
    xhttp.open("POST", getBaseUrl() + "/consent", true);
    xhttp.setRequestHeader("content-type", "application/json");
    xhttp.send(JSON.stringify(consent_request));
};

function onLoginFailed() {
    console.log("on-login-failed ...");
    hideElement(document.getElementById("login-container"));
    hideElement(document.getElementById("consent-container"));
    hideElement(document.getElementById("sign-up-container"));
    showElement(document.getElementById("login-error-container"));
};

function onLoginOk() {
    console.log("on-login-ok ...");
    available_scopes = authorization_code_response.availableScopes.values;
    selector_element = document.getElementById("scopes-selector");
    for (i = 0; i < available_scopes.length; i++) {
        console.log("populating selector:  " + available_scopes[i]);
        var div_wrapper = document.createElement("div");
        div_wrapper.setAttribute("class", "form-check");
        var input_element = document.createElement("input");
        input_element.setAttribute("class", "form-check-input");
        input_element.setAttribute("type", "checkbox");
        input_element.setAttribute("value", available_scopes[i]);
        input_element.setAttribute("id", "scope-" + i);
        input_element.setAttribute("name", "scope-" + i);
        input_element.setAttribute("onclick", "onScopeSelect(this);");
        var label_element = document.createElement("label");
        label_element.setAttribute("for", "scope-" + i);
        label_element.setAttribute("class", "form-check-label");
        var text_node = document.createTextNode(available_scopes[i]);
        label_element.appendChild(text_node);
        var br_element = document.createElement("br");
        div_wrapper.appendChild(input_element);
        div_wrapper.appendChild(label_element);
        selector_element.appendChild(div_wrapper);
        selector_element.appendChild(br_element);
        scope_input_elements[i] = input_element;
    }

    if (available_scopes.length > 0) {
        console.log("populating selector:  adding select all option");
        selector_element = document.getElementById("scopes-selector");
        var div_wrapper = document.createElement("div");
        div_wrapper.setAttribute("class", "form-group form-check");
        var input_element = document.createElement("input");
        input_element.setAttribute("class", "form-check-input");
        input_element.setAttribute("type", "checkbox");
        input_element.setAttribute("id", "scope-select-all");
        input_element.setAttribute("name", "scope-select-all");
        input_element.setAttribute("onclick", "onAllScopesSelect();");
        var label_element = document.createElement("label");
        label_element.setAttribute("for", "scope-select-all");
        label_element.setAttribute("class", "form-check-label");
        var text_node = document.createTextNode("select all");
        label_element.appendChild(text_node);
        var br_element = document.createElement("br");
        div_wrapper.appendChild(input_element);
        div_wrapper.appendChild(label_element);
        selector_element.appendChild(div_wrapper);
        selector_element.appendChild(br_element);
    } else {
        console.log("no scopes available");
    }

    hideElement(document.getElementById("login-container"));
    showElement(document.getElementById("consent-container"));
    hideElement(document.getElementById("sign-up-container"));
    hideElement(document.getElementById("login-error-container"));
};

function onStartSignUp() {
    console.log("on-start-sign-up ...");
    hideElement(document.getElementById("login-container"));
    hideElement(document.getElementById("consent-container"));
    showElement(document.getElementById("sign-up-container"));
    hideElement(document.getElementById("login-error-container"));
}

function hideElement(element) {
    element.style.display = "none";
    element.style.visibility = "hidden";
};

function showElement(element) {
    element.style.display = "block";
    element.style.visibility = "visible";
};

function getBaseUrl() {
   return window.location.origin + "/services/authentication/" + organization_id + "/" + project_id;
};

function onScopeSelect(input_element) {
   console.log("on scope selected: " + input_element.name + " " + input_element.checked);
   scopes_selected = [];
   for (i = 0; i < scope_input_elements.length; i++) {
     if (scope_input_elements[i].checked) {
         scopes_selected.push(scope_input_elements[i].value);
     }
   }
   scope_select_all_element = document.getElementById("scope-select-all");
   scope_select_all_element.checked = false;
   console.log("result scopes: " + scopes_selected);
};

function onConsentOk() {
   console.log("on Consent OK  ...");
   window.location.href = redirect_uri + "?state=" + authorization_code_response.state + "&code=" + authorization_code_response.code.code;
};

function onConsentFailed() {
   console.log("on Consent Failed ...");
};

function onAllScopesSelect() {
   console.log("on all scopes selected");
   scope_select_all_element = document.getElementById("scope-select-all");
   for (i = 0; i < scope_input_elements.length; i++) {
       scope_input_elements[i].checked = scope_select_all_element.checked;
   }
   if (scope_select_all_element.checked) {
       scopes_selected = authorization_code_response.availableScopes.values;
   } else {
       scopes_selected = [];
   }
   console.log("result scopes: " + scopes_selected);
};

