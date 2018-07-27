function loadPreview(elementId) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            document.getElementById(elementId).innerHTML = this.responseText;
        }
    };
    xhttp.open("GET", "/office/preview?id=" + elementId, true);
    xhttp.send();
}

function updatePreviewContainers() {
    var previewElements = document.getElementsByClassName("preview-container");
    for (var i=0; i < previewElements.length; i++) {
        var previewElement = previewElements.item(i);
        loadPreview(previewElement.id);
    }
}

function sendWelcomeFields() {
    var body = {'friend': document.getElementById("friend-field").value, 'word': document.getElementById("word-field").value};
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            document.getElementById("results").innerHTML = this.responseText;
        }
    };
    xhttp.open("POST", "/office/check", true);
    xhttp.send(JSON.stringify(body));
}
