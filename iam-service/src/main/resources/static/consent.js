
var scopes_selected = [];

function onLoad() {
    console.log("on-load, setting scopes ...");
    selector_element = document.getElementById("scopes-selector");
    for (i = 0; i < available_scopes.length; i++) {
        console.log("populating selector:  " + available_scopes[i]);
        var input_element = document.createElement("input");
        input_element.setAttribute("type", "checkbox");
        input_element.setAttribute("value", available_scopes[i]);
        input_element.setAttribute("id", "scope-" + i);
        input_element.setAttribute("name", "scope-" + i);
        input_element.setAttribute("onclick", "onScopeSelect(this);");
        var label_element = document.createElement("label");
        label_element.setAttribute("for", "scope-" + i);
        var text_node = document.createTextNode(available_scopes[i]);
        label_element.appendChild(text_node);
        var br_element = document.createElement("br");
        selector_element.appendChild(input_element);
        selector_element.appendChild(label_element);
        selector_element.appendChild(br_element);
    }
}

function onScopeSelect(input_element) {
   console.log("on scope selected: " + input_element.name + " " + input_element.checked);
   if (input_element.checked) {
       scopes_selected.push(input_element.value);
   } else {
       for (i = 0; i < scopes_selected.length; i++) {
           if (input_element.value == scopes_selected[i])  {
               delete scopes_selected[i];
           }
       }
   }
   onScopeChange();
}

function onScopeChange() {
   var result_scopes = "";
   for (i = 0; i < scopes_selected.length; i++) {
       if (!(!scopes_selected[i] || 0 === scopes_selected[i].length)) {
          if (result_scopes === "") {
            result_scopes = scopes_selected[i];
          } else {
            result_scopes = result_scopes + " " + scopes_selected[i];
          }
       }
   }
   console.log("result scopes: " + result_scopes);
   scope_element = document.getElementById("scope");
   scope_element.setAttribute("value", result_scopes);
}
