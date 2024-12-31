
document.addEventListener('DOMContentLoaded', function () {
    const urlParams = new URLSearchParams(window.location.search);
    let projectId = urlParams.get('projectId');
    let projectName;
    var currentFile = null;
    let chapterNo;
    let editor;
    var allFiles = [];
    let programId = urlParams.get('programId');
    loadProjectDetails();
    initializeEditor();
    document.getElementById('export-button').addEventListener('click', exportToWord);

    loadFiles();
    const chatContainer = document.getElementById('chat-container');
//const chatContainer = document.querySelector(".chat-bubbles");
    const messageInput = document.getElementById("message-input");
    let currentAssistantMessage = null;

    let sessionId = urlParams.get('sessionId');

    const submitButton = document.getElementById("submit-btn"); // Get the button element

    if (submitButton) {
        submitButton.addEventListener('click', function () {
            sendMessage();
        });
    }




    function addMessage(message, author = "You") {
        const chatItem = document.createElement("div");
        chatItem.classList.add("chat-item");
        chatItem.innerHTML = `
            <div class="row align-items-end">
                <div class="col col-lg-12">
                    <div class="chat-bubble">
                        <div class="chat-bubble-title">
                            <div class="row">
                                <div class="col chat-bubble-author">${author}</div>
                                <div class="col-auto chat-bubble-date">${new Date().toLocaleTimeString()}</div>
                            </div>
                        </div>
                        <div class="chat-bubble-body">
                            <p>${message}</p>
                        </div>
                    </div>
                </div>
            </div>
        `;
        chatContainer.appendChild(chatItem);
        chatContainer.scrollTop = chatContainer.scrollHeight;
    }



    function addChatMessageGG(message, author = "Grant Genius", prompts = []) {
        const chatItem = document.createElement("div");
        chatItem.classList.add("chat-item");

        let buttonsHTML = '';
        if (prompts.length > 0) {
            buttonsHTML = '<div class="chat-buttons">';
            prompts.forEach(prompt => {
                buttonsHTML += `<button class="btn prompt-btn">${prompt}</button>`;
            });
            buttonsHTML += '</div>';
        }

        chatItem.innerHTML = `
        <div class="row align-items-end">
            <div class="col col-lg-12">
                <div class="chat-bubble chat-bubble-me">
                    <div class="chat-bubble-title">
                        <div class="row">
                            <div class="col chat-bubble-author">${author}</div>
                            <div class="col-auto chat-bubble-date">${new Date().toLocaleTimeString()}</div>
                        </div>
                    </div>
                    <div class="chat-bubble-body">
                        <p  id="chat-message" style="white-space: pre-wrap;">${message}</p>
                       
                        ${buttonsHTML}
                        <button class="btn copy-btn" style="float: right;" title="Copy to Clipboard">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon icon-tabler icons-tabler-outline icon-tabler-copy">
                                <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                                <path d="M7 7m0 2.667a2.667 2.667 0 0 1 2.667 -2.667h8.666a2.667 2.667 0 0 1 2.667 2.667v8.666a2.667 2.667 0 0 1 -2.667 2.667h-8.666a2.667 2.667 0 0 1 -2.667 -2.667z" />
                                <path d="M4.012 16.737a2.005 2.005 0 0 1 -1.012 -1.737v-10c0 -1.1 .9 -2 2 -2h10c.75 0 1.158 .385 1.5 1" />
                            </svg>
                        </button>
                        <button class="btn add-to-editor-btn" style="float: right; margin-left: 10px;" title="Add to Editor">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon icon-tabler icons-tabler-outline icon-tabler-edit">
                            <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                            <path d="M9 7h-3a2 2 0 0 0 -2 2v9a2 2 0 0 0 2 2h9a2 2 0 0 0 2 -2v-3" />
                            <path d="M9 15h3l8.5 -8.5a1.5 1.5 0 0 0 -3 -3l-8.5 8.5v3" />
                            <line x1="16" y1="5" x2="19" y2="8" />
                        </svg>
                    </button>
                    </div>
                </div>
            </div>
        </div>
    `;
        chatContainer.appendChild(chatItem);
        chatContainer.scrollTop = chatContainer.scrollHeight;

        // Add event listeners to the new buttons
        const newButtons = chatItem.querySelectorAll('.chat-buttons .prompt-btn');
        newButtons.forEach(button => {
            button.addEventListener('click', function () {
                const messageInput = document.getElementById("message-input"); // Use the specified ID
                messageInput.value += button.innerText + ' '; // Add the clicked button's text to the input box
                messageInput.focus(); // Focus on the input box after adding the text
            });
        });

        // Add event listener to the copy button
        const copyButton = chatItem.querySelector('.chat-bubble-body .copy-btn');
        const messageElement = chatItem.querySelector('#chat-message');

        copyButton.addEventListener('click', function () {
            const messageToCopy = messageElement.innerText;
            navigator.clipboard.writeText(messageToCopy).then(() => {
                alert('Message copied to clipboard!');
            }).catch(err => {
                console.error('Failed to copy message: ', err);
            });
        });


        // Add event listener to the add to editor button
        const addToEditorButton = chatItem.querySelector('.chat-bubble-body .add-to-editor-btn');
        addToEditorButton.addEventListener('click', function () {
            const editor = tinyMCE.get('tinymce-mytextarea');
            if (editor) {
                const currentContent = editor.getContent();
                const newContent = currentContent +  `<p>${messageElement.innerHTML}</p>`;
                editor.setContent(newContent);
            } else {
                console.error('TinyMCE editor not found!');
            }
        });


        return chatItem.querySelector('.chat-bubble-body p');
    }

    function sendMessage() {

        //console.log(sessionId);
        const message = messageInput.value.trim();
        if (!message)
            return;

        // Add user message to chat
        addMessage(message);
        messageInput.value = '';

        // Create new div for assistant's response
        currentAssistantMessage = addChatMessageGG('');

        // Determine if the "Chain Previous Chapter" checkbox is checked
        const chainPreviousChapter = document.getElementById('chainPreviousChapterCheckbox').checked;
        chapterNo = document.getElementById('chapter-no').value;

        // Add this line to log the checkbox state to the console

        function formatText(text) {
            return text.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
        }



        // Construct the URL with the additional parameter if the checkbox is checked
        let url = `/api/genie/stream?message=${encodeURIComponent(message)}&projectId=${projectId}&chapterNo=${chapterNo}&programId=${programId}`;
        if (chainPreviousChapter) {
            //console.log('Checkbox state:true');

            url += `&chainPreviousChapter=true`;
        }

        // Start streaming response
        const eventSource = new EventSource(url);


        // Start streaming response
        //const eventSource = new EventSource(`/api/genie/stream?message=${encodeURIComponent(message)}&projectId=${projectId}&chapterNo=${chapterNo}&programId=${programId}`);

        eventSource.onmessage = function (event) {

            //console.log(event.data);
            
            currentAssistantMessage.textContent += event.data + ' ';
            chatContainer.scrollTop = chatContainer.scrollHeight;
        };
//
        eventSource.onerror = function (error) {
            eventSource.close();
            if (!currentAssistantMessage.textContent) {
                currentAssistantMessage.textContent = 'Error: Failed to get response';
                console.log(error);
            }
        };
    }

// Allow sending message with Enter key
    messageInput.addEventListener('keypress', function (event) {
        if (event.key === 'Enter') {
            sendMessage();
        }
    });


    function initializeEditor() {
        let options = {
            selector: '#tinymce-mytextarea',
            height: 300,
            menubar: false,
            statusbar: false,
            plugins: [
                'advlist autolink lists link image charmap print preview anchor',
                'searchreplace visualblocks code fullscreen',
                'insertdatetime media table paste code help wordcount'
            ],
            toolbar: 'undo redo | formatselect | ' +
                    'bold italic backcolor | alignleft aligncenter ' +
                    'alignright alignjustify | bullist numlist outdent indent | ' +
                    'removeformat',
            content_style: 'body { font-family: -apple-system, BlinkMacSystemFont, San Francisco, Segoe UI, Roboto, Helvetica Neue, sans-serif; font-size: 14px; -webkit-font-smoothing: antialiased; }'
        };
        if (localStorage.getItem("tablerTheme") === 'dark') {
            options.skin = 'oxide-dark';
            options.content_css = 'dark';
        }
        tinyMCE.init(options);
        document.getElementById('editor-save-button').addEventListener('click', saveFile);
    }

    function loadProjectDetails() {
        fetch(`/api/projects/${projectId}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(project => {
                    projectName = project.name;
                    document.getElementById('project-name-display').textContent = projectName;
                })
                .catch(error => {
                    console.error('Error loading project details:', error);
                    alert('Failed to load project details. Please check the console for more details.');
                });
    }
    function loadFile(chapterNo, fileId) {

        let url1 = `/api/projects/files?projectId=${projectId}&chapterNo=${chapterNo}&programId=${programId}`;

        fetch(url1)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(fileWithPrompt => {
                    currentFile = fileWithPrompt;


                    // Update the chapter name input field with the filename
                    document.getElementById('chapter-no').value = fileWithPrompt.chapterNo;
                    document.getElementById('chapter-name').value = fileWithPrompt.name;

                    // Update the description textarea with the file content
                    // For regular textarea
                    //console.log(fileWithPrompt.content.content);

                    // If using TinyMCE editor, update its content
                    if (tinyMCE.get('tinymce-mytextarea')) {
                        tinyMCE.get('tinymce-mytextarea').setContent(fileWithPrompt.content);
                    }

                    // Update any other elements or variables as needed
                    //document.getElementById('current-file-name').textContent = fileWithPrompt.name;

                    // Handle the visible prompt if needed






                    let arr = [fileWithPrompt.name];
                    const chatContainer = document.getElementById('chat-container');
                    chatContainer.innerHTML = '';
                    addChatMessageGG(message = "Please select a Prompt", author = "Grant Genius", prompts = arr);
                })
                .catch(error => {
                    console.error('Error loading file:', error);
                    alert('Failed to load file. Please check the console for more details.');
                });

        // Select the correct tab using Bootstrap JS
        const element = document.querySelector(`#chat-${fileId}-tab`);
        if (element) {
            const bs = new bootstrap.Tab(element);
            bs.show();
        }
    }
    function loadFiles() {
        fetch(`/api/projects/${projectId}/files`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(files => {
                    allFiles = files; // Store all files
                    //console.log(allFiles);
                    const fileList = document.getElementById("fileList");
                    fileList.innerHTML = ""; // Clear existing content

                    files.sort().forEach((file, index) => {
                        // Create the <a> element
                        //console.log(file.chapterNo);
                        const a = document.createElement("a");
                        a.href = `#chat-${file.chapterNo}`; // Use a unique ID for each file
                        a.className = "nav-link text-start mw-100 p-3";
                        a.id = `chat-${file.chapterNo}-tab`;
                        a.setAttribute('data-bs-toggle', 'pill');
                        a.setAttribute('role', 'tab');
                        a.setAttribute('aria-selected', 'false'); // Initially not selected

                        // Create the inner content
                        const divRow = document.createElement("div");
                        divRow.className = "row align-items-center flex-fill";

                        const divCol = document.createElement("div");
                        divCol.className = "col text-body";

                        const chapterDiv = document.createElement("div");

                        // Concatenate chapterNo and filename with a separator (e.g., "-")
                        chapterDiv.textContent = `${file.chapterNo} - ${file.name}`;

                        const promptDiv = document.createElement("div");
                        promptDiv.className = "text-secondary text-truncate w-100";

                        promptDiv.textContent = file.name || "No Prompt"; //Handle missing prompt

//                        const chapterDiv = document.createElement("div");
//                        chapterDiv.textContent = `${file.name}`; // Assuming file has a chapter property
//
//                        const promptDiv = document.createElement("div");
//                        promptDiv.className = "text-secondary text-truncate w-100";
//
//                        promptDiv.textContent = file.chapterNo || "No Prompt"; //Handle missing prompt


                        divCol.appendChild(chapterDiv);
                        //divCol.appendChild(promptDiv);

                        divRow.appendChild(divCol);

                        a.appendChild(divRow);


                        a.onclick = () => loadFile(file.chapterNo, file.id); // Pass file ID


                        fileList.appendChild(a);

                        if (index === 0) {
                            a.setAttribute('aria-selected', 'true'); // Mark as selected
                            loadFile(file.chapterNo, file.id); // Initialize editor and chat box with the first file
                            a.click();
                        }
                    });


                })
                .catch(error => {
                    console.error('Error loading files:', error);
                    alert('Failed to load files. Please check the console for more details.');
                });
    }

//
//






    function exportToWord() {
        const doc = new docx.Document({
            sections: [{properties: {}, children: []}]
        });


        // Helper function to strip HTML tags
        function stripHtmlTags(html) {
            return html.replace(/<\/?[^>]+(>|$)/g, "");
        }

        fetch(`/api/projects/${projectId}/files`)
                .then(response => response.json())
                .then(files => {




                    files.forEach(file => {
                        doc.addSection({
                            children: [
                                new docx.Paragraph({
                                    text: file.name,
                                    heading: docx.HeadingLevel.HEADING_1
                                }),
                                new docx.Paragraph({
                                    text: stripHtmlTags(file.content)
                                }),
                                new docx.Paragraph({text: ""}) // Empty paragraph for spacing
                            ]
                        });
                    });

                    return docx.Packer.toBlob(doc);
                })
                .then(blob => {
                    saveAs(blob, `${projectName}_files.docx`);
                })
                .catch(error => {
                    console.error('Error exporting to Word:', error);
                    alert('Failed to export files. Please check the console for more details.');
                });

    }


    function saveFile() {
        if (!currentFile)
            return;
        const updatedContent = tinyMCE.get('tinymce-mytextarea').getContent();
        chapterNo = document.getElementById('chapter-no').value;
        console.log(updatedContent);

        let url2 = `/api/projects/files?projectId=${projectId}&chapterNo=${chapterNo}`;
        fetch(url2, {
            method: 'PUT',
            headers: {
                'Content-Type': 'text/plain'
            },
            body: tinyMCE.get('tinymce-mytextarea').getContent()
        })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(updatedFile => {
                    console.log('File saved:', updatedFile);
                    alert('File saved successfully!');
                })
                .catch(error => {
                    console.error('Error saving file:', error);
                    alert('Failed to save file. Please check the console for more details.');
                });
    }
}
);


//
//function getProjectIdFromUrl() {
//    const urlParams = new URLSearchParams(window.location.search);
//    return urlParams.get('projectId');
//}
//
//function getProgramIdFromUrl() {
//    const urlParams = new URLSearchParams(window.location.search);
//    return urlParams.get('programId');
//}
//
//function initializeEditor() {
//    let options = {
//        selector: '#tinymce-mytextarea',
//        height: 300,
//        menubar: false,
//        statusbar: false,
//        plugins: [
//            'advlist autolink lists link image charmap print preview anchor',
//            'searchreplace visualblocks code fullscreen',
//            'insertdatetime media table paste code help wordcount'
//        ],
//        toolbar: 'undo redo | formatselect | ' +
//                'bold italic backcolor | alignleft aligncenter ' +
//                'alignright alignjustify | bullist numlist outdent indent | ' +
//                'removeformat',
//        content_style: 'body { font-family: -apple-system, BlinkMacSystemFont, San Francisco, Segoe UI, Roboto, Helvetica Neue, sans-serif; font-size: 14px; -webkit-font-smoothing: antialiased; }'
//    };
//    if (localStorage.getItem("tablerTheme") === 'dark') {
//        options.skin = 'oxide-dark';
//        options.content_css = 'dark';
//    }
//    tinyMCE.init(options);
//    //document.getElementById('save-button').addEventListener('click', saveFile);
//}
//
//function loadProjectDetails() {
//    fetch(`/api/projects/${projectId}`)
//            .then(response => {
//                if (!response.ok) {
//                    throw new Error(`HTTP error! status: ${response.status}`);
//                }
//                return response.json();
//            })
//            .then(project => {
//                projectName = project.name;
//                document.getElementById('project-name-display').textContent = projectName;
//            })
//            .catch(error => {
//                console.error('Error loading project details:', error);
//                alert('Failed to load project details. Please check the console for more details.');
//            });
//}
//
//function loadFiles() {
//    fetch(`/api/projects/${projectId}/files`)
//        .then(response => {
//            if (!response.ok) {
//                throw new Error(`HTTP error! status: ${response.status}`);
//            }
//            return response.json();
//        })
//        .then(files => {
//            allFiles = files; // Store all files
//            console.log(allFiles);
//            const fileList = document.getElementById("fileList");
//            fileList.innerHTML = ""; // Clear existing content
//
//            files.sort().forEach(file => {
//                // Create the <a> element
//                const a = document.createElement("a");
//                a.href = `#chat-${file.id}`; // Use a unique ID for each file
//                a.className = "nav-link text-start mw-100 p-3";
//                a.id = `chat-${file.id}-tab`;
//                a.setAttribute('data-bs-toggle', 'pill');
//                a.setAttribute('role', 'tab');
//                a.setAttribute('aria-selected', 'false'); // Initially not selected
//
//                // Create the inner content
//                const divRow = document.createElement("div");
//                divRow.className = "row align-items-center flex-fill";
//
//                const divCol = document.createElement("div");
//                divCol.className = "col text-body";
//
//                const chapterDiv = document.createElement("div");
//                chapterDiv.textContent = `Chapter ${file.name}`; // Assuming file has a chapter property
//
//                const promptDiv = document.createElement("div");
//                promptDiv.className = "text-secondary text-truncate w-100";
//                promptDiv.textContent = file.content || "No Prompt"; //Handle missing prompt
//
//
//                divCol.appendChild(chapterDiv);
//                divCol.appendChild(promptDiv);
//                divRow.appendChild(divCol);
//
//                a.appendChild(divRow);
//
//
//                a.onclick = () => loadFile(file.name, file.id); // Pass file ID
//
//
//                fileList.appendChild(a);
//            });
//
//
//        })
//        .catch(error => {
//            console.error('Error loading files:', error);
//            alert('Failed to load files. Please check the console for more details.');
//        });
//}
//
////
//// Example usage (assuming you have a loadFile function)
//function loadFile(fileName, fileId) {
//  // Add logic to load the file content based on fileId
//  console.log(`Loading file: ${fileName} with ID ${fileId}`);
//  // ... your file loading logic here ...
//  //  e.g., select the correct tab using bootstrap js
//  const element = document.querySelector(`#chat-${fileId}-tab`);
//  if(element){
//    const element2 = document.querySelector(`#chat-${fileId}`);
//    if(element2){
//      const bs = new bootstrap.Tab(element);
//      bs.show();
//    }
//  }
//}
//
//
//
//
//function addChatMessageGG(message, author = "Grant Genius", prompts = []) {
//    const chatItem = document.createElement("div");
//    chatItem.classList.add("chat-item");
//
//    let buttonsHTML = '';
//    if (prompts.length > 0) {
//        buttonsHTML = '<div class="chat-buttons">';
//        prompts.forEach(prompt => {
//            buttonsHTML += `<button class="btn prompt-btn">${prompt}</button>`;
//        });
//        buttonsHTML += '</div>';
//    }
//
//    chatItem.innerHTML = `
//        <div class="row align-items-end">
//            <div class="col col-lg-12">
//                <div class="chat-bubble chat-bubble-me">
//                    <div class="chat-bubble-title">
//                        <div class="row">
//                            <div class="col chat-bubble-author">${author}</div>
//                            <div class="col-auto chat-bubble-date">${new Date().toLocaleTimeString()}</div>
//                        </div>
//                    </div>
//                    <div class="chat-bubble-body">
//                        <p id="chat-message">${message}</p>
//                        ${buttonsHTML}
//                        <button class="btn copy-btn" style="float: right;" title="Copy to Clipboard">
//                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon icon-tabler icons-tabler-outline icon-tabler-copy">
//                                <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
//                                <path d="M7 7m0 2.667a2.667 2.667 0 0 1 2.667 -2.667h8.666a2.667 2.667 0 0 1 2.667 2.667v8.666a2.667 2.667 0 0 1 -2.667 2.667h-8.666a2.667 2.667 0 0 1 -2.667 -2.667z" />
//                                <path d="M4.012 16.737a2.005 2.005 0 0 1 -1.012 -1.737v-10c0 -1.1 .9 -2 2 -2h10c.75 0 1.158 .385 1.5 1" />
//                            </svg>
//                        </button>
//                    </div>
//                </div>
//            </div>
//        </div>
//    `;
//    chatContainer.appendChild(chatItem);
//    chatContainer.scrollTop = chatContainer.scrollHeight;
//
//    // Add event listeners to the new buttons
//    const newButtons = chatItem.querySelectorAll('.chat-buttons .prompt-btn');
//    newButtons.forEach(button => {
//        button.addEventListener('click', function () {
//            const messageInput = document.getElementById("message-input"); // Use the specified ID
//            messageInput.value += button.innerText + ' '; // Add the clicked button's text to the input box
//            messageInput.focus(); // Focus on the input box after adding the text
//        });
//    });
//
//    // Add event listener to the copy button
//    const copyButton = chatItem.querySelector('.chat-bubble-body .copy-btn');
//    const messageElement = chatItem.querySelector('#chat-message');
//
//    copyButton.addEventListener('click', function () {
//        const messageToCopy = messageElement.innerText;
//        navigator.clipboard.writeText(messageToCopy).then(() => {
//            alert('Message copied to clipboard!');
//        }).catch(err => {
//            console.error('Failed to copy message: ', err);
//        });
//    });
//
//    return chatItem.querySelector('.chat-bubble-body p');
//}
//
//
//
//
//
//
////
////function loadFile(fileName) {
////    fetch(`/api/projects/${projectId}/files/${fileName}?programId=${programId}`)
////            .then(response => {
////                if (!response.ok) {
////                    throw new Error(`HTTP error! status: ${response.status}`);
////                }
////                return response.json();
////            })
////            .then(fileWithPrompt => {
////                currentFile = fileWithPrompt;
////                editor.setData(fileWithPrompt.content);
////                document.getElementById('current-file-name').textContent = fileWithPrompt.name;
////                // Handle the visible prompt
////                //console.log(fileWithPrompt.visiblePrompt);
////                updateVisiblePrompt(fileWithPrompt.visiblePrompt);
////            })
////            .catch(error => {
////                console.error('Error loading file:', error);
////                alert('Failed to load file. Please check the console for more details.');
////            });
////}
//
////function updateVisiblePrompt(prompt) {
////    const promptContainer = document.getElementById('visible-prompt-container');
////    if (prompt) {
////        promptContainer.innerHTML = `
////            <div class="visible-prompt-chip" onclick="addPromptToInput(this)">
////                ${prompt}
////            </div>
////        `;
////        promptContainer.style.display = 'block';
////    } else {
////        promptContainer.innerHTML = '';
////        promptContainer.style.display = 'none';
////    }
////}
//
//function addPromptToInput(element) {
//    const chatInput = document.getElementById('chatInput');
//    chatInput.value = element.textContent.trim();
//    chatInput.focus();
//}
//
//function saveFile() {
//    if (!currentFile)
//        return;
//    const updatedContent = editor.getData();
//    fetch(`/api/projects/${projectId}/files/${currentFile.name}`, {
//        method: 'PUT',
//        headers: {
//            'Content-Type': 'application/json'
//        },
//        body: JSON.stringify({
//            ...currentFile,
//            content: updatedContent
//        })
//    })
//            .then(response => {
//                if (!response.ok) {
//                    throw new Error(`HTTP error! status: ${response.status}`);
//                }
//                return response.json();
//            })
//            .then(updatedFile => {
//                console.log('File saved:', updatedFile);
//                alert('File saved successfully!');
//            })
//            .catch(error => {
//                console.error('Error saving file:', error);
//                alert('Failed to save file. Please check the console for more details.');
//            });
//}
//
//
//
//function exportToWord() {
//    const doc = new docx.Document({
//        sections: [{properties: {}, children: []}]
//    });
//
//    fetch(`/api/projects/${projectId}/files`)
//            .then(response => response.json())
//            .then(files => {
//
//                files.forEach(file => {
//                    doc.addSection({
//                        children: [
//                            new docx.Paragraph({
//                                text: file.name,
//                                heading: docx.HeadingLevel.HEADING_1
//                            }),
//                            new docx.Paragraph({
//                                text: file.content
//                            }),
//                            new docx.Paragraph({text: ""}) // Empty paragraph for spacing
//                        ]
//                    });
//                });
//
//                return docx.Packer.toBlob(doc);
//            })
//            .then(blob => {
//                saveAs(blob, `${projectName}_files.docx`);
//            })
//            .catch(error => {
//                console.error('Error exporting to Word:', error);
//                alert('Failed to export files. Please check the console for more details.');
//            });
//}
////prompt
//
//
//async function initializeChat() {
//    chatMessages = document.getElementById('chatMessages');
//    chatInput = document.getElementById('chatInput');
//    const sendButton = document.getElementById('sendMessage');
//    sendButton.addEventListener('click', sendChatMessage);
//    chatInput.addEventListener('keypress', function (e) {
//        if (e.key === 'Enter') {
//            sendChatMessage();
//        }
//    });
//    if (programId) {
//        fetch(`/api/ai/initiate/${programId}`)
//                .then(response => {
//                    if (!response.ok) {
//                        throw new Error('Failed to initialize chat');
//                    }
//                    return response.json();
//                })
//                .then(data => {
//
//                    addMessageToChat('Chatbot', data.initialGreeting);
//                })
//                .catch(error => {
//                    console.error('Error initializing chat:', error);
//                    addMessageToChat('System', 'Error initializing chat. Please try again later.');
//                });
//    } else {
//        console.error('Program ID is missing');
//        addMessageToChat('System', 'Error: Program ID is missing. Unable to initialize chat.');
//    }
//}
//
//function sendChatMessage() {
//    const message = chatInput.value.trim();
//    if (message) {
//        addMessageToChat('You', message);
//        chatInput.value = '';
//        // Here you would typically send the message to your chatbot API
//        // and then add the response to the chat
////                    setTimeout(() => {
////                        addMessageToChat('Chatbot', 'This is a placeholder response from the chatbot.');
////                    }, 1000);
//        const chapterNo = currentFile ? currentFile.name : 'Unknown';
//        fetch(`/api/ai/interact`, {
//            method: 'POST',
//            headers: {
//                'Content-Type': 'application/json',
//            },
//            body: JSON.stringify({
//
//                programId: programId,
//                chapterNo: chapterNo,
//                userMessage: message,
//                projectId: projectId
//
//            })
//        })
//                .then(response => {
//                    if (!response.ok) {
//                        throw new Error('Failed to get response from chatbot');
//                    }
//                    return response.json();
//                })
//                .then(data => {
//                    //console.log('Received data:', data);
//                    if (data && data.message) {
//                        addMessageToChat('Chatbot', data.message);
//                    } else {
//                        addMessageToChat('Chatbot', 'Invalid response format from server');
//                    }
//                })
//                .catch(error => {
//                    console.error('Error sending message:', error);
//                    addMessageToChat('System', 'Error getting response from chatbot. Please try again.');
//                });
//
//    }
//}
//
//function addMessageToChat(sender, message) {
//    const messageElement = document.createElement('div');
//    messageElement.className = 'chat-message';
//    messageElement.innerHTML = `
//                        <div class="message-content col-md-10">
//                                              <div class="message-header">
//    <strong class="sender">${sender}</strong>
//  </div>
//  <div class="message-content">
//    <pre>${message.replace(/\n/g, '<br>').replace(/(\d+)/g, '<span class="number">$1</span>')}</pre>
//  </div></div>
//                        <div class="message-actions col-md-2">
//                            <button class="btn-copy" data-bs-toggle="tooltip" data-bs-placement="top" title="Copy to clipboard"><i class="fas fa-copy"></i></button>
//                            <button class="btn-add" data-bs-toggle="tooltip" data-bs-placement="top" title="Add to editor"><i class="fas fa-plus"></i></button>
//                            <button class="btn-upvote" data-bs-toggle="tooltip" data-bs-placement="top" title="Upvote"><i class="fas fa-arrow-up"></i></button>
//                            <button class="btn-downvote" data-bs-toggle="tooltip" data-bs-placement="top" title="Downvote"><i class="fas fa-arrow-down"></i></button>
//                        </div>
//                    `;
//    chatMessages.appendChild(messageElement);
//    chatMessages.scrollTop = chatMessages.scrollHeight;
//    // Initialize tooltips for the new message
//    var tooltipTriggerList = [].slice.call(messageElement.querySelectorAll('[data-bs-toggle="tooltip"]'));
//    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
//        return new bootstrap.Tooltip(tooltipTriggerEl);
//    });
//    // Add event listeners for the action buttons
//    messageElement.querySelector('.btn-copy').addEventListener('click', () => copyToClipboard(message));
//    messageElement.querySelector('.btn-add').addEventListener('click', () => addToEditor(message));
//    messageElement.querySelector('.btn-upvote').addEventListener('click', () => voteMessage(1));
//    messageElement.querySelector('.btn-downvote').addEventListener('click', () => voteMessage(-1));
//}
//
//function copyToClipboard(text) {
//    navigator.clipboard.writeText(text).then(() => {
//        alert('Copied to clipboard!');
//    }).catch(err => {
//        console.error('Failed to copy: ', err);
//    });
//}
//
//function addToEditor(text) {
//    const currentContent = editor.getData();
//    editor.setData(currentContent + '<p>' + text + '</p>');
//}
//
//function voteMessage(vote) {
//    // Here you would typically send the vote to your backend
//    console.log(`Message voted: ${vote}`);
//    alert(`Thank you for your feedback! (${vote > 0 ? 'Upvote' : 'Downvote'})`);
//}
//
//
//



