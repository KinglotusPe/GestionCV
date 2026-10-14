// Ayuda de edición del resumen del CV; no publica ni guarda automáticamente.
document.addEventListener('DOMContentLoaded', () => {
 const summary=document.querySelector('form[action$="/graduate/cv"] textarea[name="summary"]');
 if(!summary)return;
 const counter=document.createElement('small');counter.className='cv-character-count text-muted';counter.setAttribute('aria-live','polite');summary.after(counter);
 function update(){counter.textContent=summary.value.length+' caracteres';}
 summary.addEventListener('input',update);update();
});
