$.extend($.fn.validatebox.defaults.rules, {
    isNumberAndLength: {
        validator: function(value, param){
            return !isNaN(value) && value.length == 4;
        },
        message: 'Please enter number at between 0001 - 9999'
    }
});
