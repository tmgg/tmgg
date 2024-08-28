import React from 'react';

import './style.less';

export function showContextMenu(e, menu, callback) {
  const nav = _initElement();

  nav.style.left = e.clientX + 'px';
  nav.style.top = e.clientY + 'px';
  nav.style.display = 'block';

  const ul = document.createElement('ul');

  for (let item of menu) {
    const li = document.createElement('li');

    const span = document.createElement('span');

    span.innerText = item;

    li.appendChild(span);

    li.addEventListener('click', () => {
      callback(item);

      nav.style.display = 'none';
    });

    ul.appendChild(li);
  }

  nav.innerHTML = '';
  nav.appendChild(ul);
}

function _initElement() {
  let nav = document.getElementById('jt-context-menu');
  if (!nav) {
    nav = document.createElement('nav');
    nav.setAttribute('id', 'jt-context-menu');

    document.body.appendChild(nav);

    document.body.addEventListener('click', () => {
      nav.style.display = 'none';
    });
  }

  return nav;
}
