document.addEventListener('DOMContentLoaded', () => {


    document.getElementById('addbtn').addEventListener('click', function () {
        var newitem = document.getElementById('add').value;
        var uniqid = Math.round(new Date().getTime() + (Math.random() * 100));
        var li = document.createElement('li');
        li.id = uniqid; 
        li.className = 'list-group-item';
        li.innerHTML = '<input type="button" data-id="'+uniqid+'" class="listelement" value="X" /> ' + newitem;
        var hiddenInput = document.createElement('input'); 
        hiddenInput.type = 'hidden'; 
        hiddenInput.name = 'visiblePrompt'; 
        hiddenInput.setAttribute('th:value', newitem); 
        hiddenInput.setAttribute('th:field', "*{visiblePrompt}");
        hiddenInput.value = newitem; li.appendChild(hiddenInput);
        document.getElementById('list').appendChild(li);
        document.getElementById('add').value = '';
    });
    document.getElementById('list').addEventListener('click', function (e) {
        if (e.target && e.target.classList.contains('listelement')) {
            var elemid = e.target.getAttribute('data-id');
            var elem = document.getElementById(elemid);
            elem.parentNode.removeChild(elem);
        }
    });
});