DatDecodeUtil = {

checkResultSchema:function(jstr) {

    try {
        return DatDecodeUtil.doCheckResultSchema(jstr);
    } catch(err) {
        return err;
    }
},

checkSampleCases:function(jstr, jschema) {

    try {
        return DatDecodeUtil.doCheckSampleCases(jstr, jschema);
    } catch(err) {
        return err;
    }
},

runSampleCases:function(jcase, script) {

    try {
        return DatDecodeUtil.doRunSampleCases(jcase, script);
    } catch(err) {
        return err;
    }
},


doCheckResultSchema:function(jstr) {
    var schema = JSON.parse(jstr);
    var result = {name:"返回值模式错误", message:""};
    var hasSno = false;
    var hasField = false;
    for(key in schema) {
        hasSno = true;
        // 1. sensorNo:必须是数字
        if (isNaN(key) || key == "") {
             result.message = "sensorNo:必须是数字(" + key + ")";
             return result;
        }

        var item = schema[key];
        //  
        if (item.type == "ref") {
            var refSno = item.refsno;
            if(!refSno) {
                result.message = "refsno:缺失 (" + key + ")";
                return result;
            }

            if(refSno==key || !schema[refSno]) {
                result.message = "refsno:不存在 (" + key + ")";
                return result;
            }

            var refItem = schema[refSno];
            if (!refItem.type || refItem.type!="metric") {
                result.message = "refsno：必须为实体(" + key + "," + refSno + ")";
                return result;
            }

            continue;
        }
        if (!item.type || item.type!="metric") {
            result.message = "type='metric'固定(" + key + ")";
            return result;
        }

        // 3. field
        if (!item.field ) {
            result.message =  "field必须项目缺失(" + key + ")";
            return result;
        }
        var field = item.field;

        for(name in field) {
            hasField = true;
            if (name == "") {
                result.message =  "field 字段名称不能为空(" + key + "->" + name + ")";
                return result;
            }

            if (!field[name].display) {
                result.message =  "field.display 必须项目缺失(" + key + "->" + name + ")";
                return result;
            }
            if (!field[name].format) {
                result.message =  "field.format 必须项目缺失(" + key + "->" + name + ")";
                return result;
            }
            if (!field[name].unit) {
                result.message =  "field.unit 必须项目缺失(" + key + "->" + name + ")";
                return result;
            }
        }
    }

    if (!hasSno) {
        result.message =  "sensorNo 必须项目缺失";
        return result;
    }
    if (!hasField) {
        result.message =  "field 必须项目缺失";
        return result;
    }
    return null;

},

doCheckSampleCases:function(jstr, jschema) {

    var cases = JSON.parse(jstr);
    var schema = JSON.parse(jschema);
    
    var result = {name:"测试用例错误", message:""};

    if(!cases[0]) {
        result.message =  "测试用例必须是非空数组";
        return result;
    }

    for(var i =0; i<cases.length; i++) {
        var item = cases[i];
        if (!item["input"]) {
            result.message =  "input必须项目缺失(index=" + i +  ")";
            return result;
        }
        if (!item["output"]) {
            result.message =  "output必须项目缺失(index=" + i +  ")";
            return result;
        }


        var inputBytes = hexStringToBytes(item["input"]);
        var inputStr = byteArray2hexStr(inputBytes).toUpperCase().replaceAll(" ", "");;
        
        if (item["input"].toUpperCase().replaceAll(" ", "") != inputStr) {
            result.message =  "input hexString 无效(index=" + i +  ")";
            return result;
        }

        if (item["output"] == "null") { 
            continue;
        }

        // 一次接收多个传感器数据
        var outputList = [];
        if (item["output"][1]) {
            outputList = item["output"];
        } else {
            outputList[0] = item["output"];
        }

        for(var j=0;j<outputList.length;j++) {
            var outputItem = outputList[j];


            if (!outputItem["sno"]) {
                result.message =  "output.sno 必须项目缺失(index=" + i +  ", " + j + ")";
                return result;
            }
            if (!outputItem["data"]) {
                result.message =  "output.data 必须项目缺失(index=" + i +  ", " + j + ")";
                return result;
            }
    
            var sno = outputItem["sno"];
            var data = outputItem["data"];
            if (!schema[sno]) {
                result.message =  "output.sno:" + sno + " 在schema中未定义(index=" + i +  ", " + j + ")";
                return result;
            }
    
            var hasField = false;
            for (key in data) {
                hasField = true;
                
                if(schema[sno]["type"]=="ref") {
                    sno = schema[sno]["refsno"];
                }

                if (!schema[sno]["field"][key]) {
                    result.message =  "output.data field: " + key + " 在schema中未定义(index=" + i +  ", " + j + ")";
                    return result;
                }
    
                if (isNaN(data[key])) {
                    result.message =  "output.data: " + key + " 必须是数字类型的值(index=" + i +  ", " + j + ")";
                    return result;
                }
            }
    
    
            if (!hasField) {
                result.message =  "output.data 必须项目缺失(index=" + i +  ", " + j + ")";
                return result;
            }
        }


    }


    return null;
},

doRunSampleCases:function(jcase, script) {

    var result = {name:"运行结果错误", message:""};

    var cases = JSON.parse(jcase);
    // var fnId = "decodeDat_" + guid();
    eval(script);
    for(i in cases) {
        var item = cases[i];
        var actual = decodeDat(hexStringToBytes(item.input));
        var expected = item.output;
        if (actual == null ) {
            if (expected!="null"){
                result.message =  "期待：" + JSON.stringify(expected) + "\n实际：null"  + "\n(caseIdx=" + i +  ")";
                return result;
            } else {
                continue;
            }
        }
        
        expected = JSON.stringify(expected);
        if (actual != expected) {
            result.message =  "期待：" + expected + "\n实际：" + actual + "\n(caseIdx=" + i +  ")";
            return result;
        } 

    }

    return null;
}






} // end