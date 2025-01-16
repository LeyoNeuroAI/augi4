document.addEventListener('DOMContentLoaded', function () {
    const chatContainer = document.getElementById('chat-container');
//const chatContainer = document.querySelector(".chat-bubbles");
    const messageInput = document.getElementById("message-input");
    
  const url = window.location.href;

// Call the function to get the "DevGenius" value
const geniusName = new URL(url).searchParams.get('name');
    // Get the button element

    const sessionInput = document.getElementById("sessionInput");
    let currentAssistantMessage = null;
//    const urlParams = new URLSearchParams(window.location.search);

    const submitButton = document.getElementById("submit-btn"); // Get the button element

    // ... rest of your code ...

    submitButton.addEventListener('click', sendMessage); // Add event listener

   
document.querySelectorAll('.custom-link').forEach(function(link) {
    link.addEventListener('click', function(event) {
        event.preventDefault();
        window.location.href = this.href;
    });
});




    // Function to add clicked button text to message-input
    function addButtonTextToInput() {
        document.querySelectorAll('.prompt-btn').forEach(button => {
            button.addEventListener('click', () => {
                messageInput.value = button.textContent;
            });
        });
    }

    addButtonTextToInput();


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



    function addChatMessageGG(message = "Hello How can I assist you", author = geniusName, prompts = []) {
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

        return chatItem.querySelector('.chat-bubble-body p');
    }

    function sendMessage() {

        const sessionId = sessionInput.value;
        console.log(sessionId);
        const message = messageInput.value.trim();
        if (!message)
            return;

        // Add user message to chat
        addMessage(message);
        messageInput.value = '';

        // Create new div for assistant's response
        currentAssistantMessage = addChatMessageGG('');

        // Start streaming response
        const eventSource = new EventSource(`/professional/stream?sessionId=${sessionId}&name=${geniusName}&message=${encodeURIComponent(message)}`);

        eventSource.onmessage = function (event) {
            currentAssistantMessage.textContent += event.data + ' ';
            chatContainer.scrollTop = chatContainer.scrollHeight;
        };





        eventSource.onerror = function (error) {
            eventSource.close();
            if (!currentAssistantMessage.textContent) {
                currentAssistantMessage.textContent = 'Error: Failed to get response';
                console.log(error);
                 console.log('Connection state:', eventSource.readyState);
  console.log('Status:', event.target.status);
  console.log('Headers:', event.target.getAllResponseHeaders());
            }
        };
    }

// Allow sending message with Enter key
    messageInput.addEventListener('keypress', function (event) {
        if (event.key === 'Enter') {
            sendMessage();
        }
    });
}
);
                