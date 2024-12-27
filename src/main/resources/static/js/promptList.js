document.addEventListener('DOMContentLoaded', () => {
    const visiblePromptInput = document.getElementById('visiblePromptInput');
    const addVisiblePromptButton = document.getElementById('addVisiblePrompt');
    const visiblePromptList = document.getElementById('visiblePromptList');
    const form = document.getElementById('myForm');

    addVisiblePromptButton.addEventListener('click', () => {
        const itemValue = visiblePromptInput.value.trim();
        if (itemValue && itemValue.length <= 255) {
            const listItem = document.createElement('li');
            listItem.classList.add('list-group-item', 'd-flex', 'justify-content-between', 'align-items-center');
            listItem.textContent = itemValue;
            const removeButton = document.createElement('button');
            removeButton.classList.add('btn', 'btn-danger', 'btn-sm');
            removeButton.textContent = 'Remove';
            removeButton.addEventListener('click', () => {
                listItem.remove();
            });
            listItem.appendChild(removeButton);
            visiblePromptList.appendChild(listItem);
            visiblePromptInput.value = '';
        } else {
            alert('Visible prompt item must be 255 characters or less.');
        }
    });

    form.addEventListener('submit', (event) => {
        event.preventDefault();
        const visiblePromptItems = Array.from(visiblePromptList.children).map(li => li.firstChild.textContent);
        const visiblePromptField = document.createElement('input');
        visiblePromptField.type = 'hidden';
        visiblePromptField.name = 'visiblePrompt';
        visiblePromptField.value = visiblePromptItems;
        form.appendChild(visiblePromptField);
        form.submit();
    });
});
