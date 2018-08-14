$.extend($.fn.validatebox.defaults.rules, {
    isNumberAndLength: {
        validator: function(value, param){
            return !isNaN(value) && value.length == 4;
        },
        message: 'Please enter number at between 0001 - 9999'
    }
});

$.extend($.fn.validatebox.defaults.rules, {
    isDateAndFormat: {
        validator: function(value, param){
            return !isNaN(value) && value.length == 4;
        },
        message: 'Please enter a date in format \'yyyy-mm-dd\''
    }
});

$.fn.datebox.defaults.parser = function(s){
    alert(this);
	var t = Date.parse(s);
	if (!isNaN(t)){
		return new Date(t);
	} else {
		return new Date();
	}
}