function handleKeyPress(e) {
    var key = e.keyCode || e.which;
    if (key == 13) {
        doSearch();
    }
}