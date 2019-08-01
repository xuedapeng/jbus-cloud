// 

// byte => float  IEEE754
function decodeFloat(byteArray) {
    var sign = parseSign(byteArray);
    var exponent = parseExponent(byteArray);
    var mantissa = parseSignificand(byteArray);
    var num = sign * exponent * mantissa;
    return num;
};

function parseSign(byteArray) {
    if(byteArray[0]&0x80)
        return -1;
    return 1;
}

function parseExponent(byteArray) {
    var ex = (byteArray[0] & 0x7F);
    ex = ex << 1;
    
    if(0!=(byteArray[1] & 0x80))
        ex += 0x01;
    
    ex = Math.pow(2, ex-127);
    return ex;
}

function parseSignificand(byteArray) {
    var num=0;
    var bit;
    var mask = 0x40;
    for(var i=1; i<8; i++) {
        if(0!=(byteArray[1]&mask)) 
            num += 1 / Math.pow(2, i);
        mask = mask >> 1;
    }
    mask = 0x80;
    for(var j=0; j<8; j++) {
        if(0!=(byteArray[2]&mask))
            num += 1 / Math.pow(2, j+8);
        mask = mask >> 1;
    }
    mask = 0x80;
    for(var k=0; k<8; k++) {
        if(0!=(byteArray[2]&mask))
            num += 1 / Math.pow(2, k+16);
        mask = mask >> 1;
    }
    return (num+1);
}