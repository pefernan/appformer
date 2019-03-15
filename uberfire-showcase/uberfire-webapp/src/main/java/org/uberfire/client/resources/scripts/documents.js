appformer = {
    forms: {
        Documents: function Documents() {},
        Document: function Document(name, url, size) {
            this.name = name;
            this.url = url;
            this.size = size;
        }
    }
};

appformer.forms.Documents.get = function() {
    return new appformer.forms.Documents();
};

appformer.forms.Documents.prototype.preventEvents = function (event) {
    event.preventDefault();
    event.stopPropagation();
};

appformer.forms.Documents.prototype.dropFiles = function (event) {
    this.preventEvents(event);
    if (event.dataTransfer.items) {
        for (var i = 0; i < event.dataTransfer.items.length; i++) {
            if (event.dataTransfer.items[i].kind === 'file') {
                var item = event.dataTransfer.items[i];
                var isFile = true;
                if (typeof (item.webkitGetAsEntry) == "function") {
                    isFile = item.webkitGetAsEntry().isFile;
                } else if (typeof (item.getAsEntry) == "function") {
                    isFile =  item.getAsEntry().isFile;
                }

                if(isFile) {
                    this.dropFile(item.getAsFile());
                }
            }
        }
    } else {
        this.dropFilesList(event.dataTransfer.files);
    }
};

appformer.forms.Documents.prototype.dropFilesList = function(fileList) {
    for (var i = 0; i < fileList.length; i++) {
        this.dropFile(fileList[i]);
    }
};

appformer.forms.Documents.prototype.dropFile = function (file) {
    var callback = this.onDropCallback;
    var reader = new FileReader();
    reader.onload = function (event) {
        if(callback != null) {
            var doc = new appformer.forms.Document(file.name, event.target.result, file.size);
            callback(doc);
        }
    };
    reader.readAsDataURL(file);
};

appformer.forms.Documents.prototype.bind = function(element) {
    if(!element) {
        throw "Cannot bind documents upload to a null element";
    }
    if (!element.tagName) {
        throw "Cannot bind documents upload to a non html element";
    }

    var tag = element.tagName.toUpperCase();

    if(tag === "DIV") {
        this.divElement = element;
        ['click', 'drag', 'dragstart', 'dragend', 'dragover', 'dragenter', 'dragleave'].forEach(eventName => this.divElement.addEventListener(eventName, event => this.preventEvents(event)));
        this.divElement.addEventListener('drop', event => this.dropFiles(event));
        return this;
    } else if (tag === "INPUT" && element.type.toUpperCase() == "FILE") {
        this.inputElement = element;
        this.inputElement.addEventListener('change', event => this.dropFilesList(event.target.files));
        return this;
    }

    throw "Cannot bind documents to " + element.tagName + " elements";
};

appformer.forms.Documents.prototype.onDrop = function(callback) {
    this.onDropCallback = callback;
    return this;
};