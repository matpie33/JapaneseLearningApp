
function createTooltipDiv (polishKeyword){
    var div = document.createElement('div');
    div.style.border = '2px solid black';
    div.style.position = 'fixed';
    div.style.top = (rect.top - 50) + 'px';
    div.style.fontSize = '30px';
    div.style.background = 'white';
    div.style.wordBreak='break-word';
    div.style.padding = '0px 4px';
    if (!polishKeyword || 0 === polishKeyword.length){
        div.innerHTML = 'Brak informacji';
    }
    else{
        div.innerHTML = polishKeyword;
    }

    div.style.left = rect.left + 'px';
    return div;
}


