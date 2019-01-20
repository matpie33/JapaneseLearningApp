
var selection = window.getSelection().toString().toLowerCase();
var heisigKanjiId = getKanjiKeywords().indexOf(selection) + 1;
if (heisigKanjiId >= 0){
    var polishKeyword = ApplicationController.getKanjiKeywordById(heisigKanjiId);

    var selection = window.getSelection();
    range = selection.getRangeAt(0);
    rect = range.getBoundingClientRect();

    var div = createTooltipDiv(polishKeyword);
    document.body.appendChild(div);

    var body = document.getElementsByTagName("BODY")[0];
    body.onmousemove = function (){
        div.style.display = 'none';
    }
}




