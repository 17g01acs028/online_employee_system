package org.example.libs;

import org.json.JSONArray;
import org.json.JSONObject;

public class Response {
    private boolean status;
    private String message;

    private JSONArray obj_message;
    private int code;

    public Response(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Response(int code, JSONArray obj_message) {
        this.code = code;
        this.obj_message = obj_message;
    }

    // Getters for status and message
    public boolean getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }

    public void setObj_message(JSONArray obj_message) {
        this.obj_message = obj_message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    //Getters for code
    public int getCode() {
        return code;
    }

    //Getter for object
    public JSONArray getResponse(){
        return obj_message;
    }

    // Setters
    public void setStatus(boolean status) {
        this.status = status;
    }
    public String getFieldValue(String fieldName) {
        if (this.obj_message != null && this.obj_message.length() > 0) {
            for (int i = 0; i < this.obj_message.length(); i++) {
                JSONObject obj = this.obj_message.getJSONObject(i);
                if (obj.has(fieldName)) { // Check if the field exists
                    return obj.optString(fieldName, null); // Return its value
                }
            }
        }
        return null; // Return null if field is not found or obj_message is not set
    }

    public String getPeriodId() {
        if (this.obj_message != null && this.obj_message.length() > 0) {
            JSONObject firstItem = this.obj_message.getJSONObject(0); // Assuming the first item contains the period_id
            return firstItem.optString("period_id", null); // Returns period_id or null if not found
        }
        return null; // Returns null if obj_message is empty or not set
    }

    public String getValueByKey(String key) {
        if (this.obj_message != null && this.obj_message.length() > 0) {
            JSONObject firstItem = this.obj_message.getJSONObject(0); // Assuming the needed key is in the first item
            return firstItem.optString(key, null); // Returns the value associated with the key or null if not found
        }
        return null; // Returns null if obj_message is empty or not set
    }

    public void setMessage(String message) {
        this.message = message;
    }
    @Override
    public String toString() {
        JSONObject responseJson = new JSONObject();
        responseJson.put("status", status);
        responseJson.put("code", code);
        responseJson.put("message", message);

        // Assuming obj_message is a JSONArray of JSONObjects
        if (obj_message != null && obj_message.length() > 0) {
            responseJson.put("data", obj_message);
        } else {
            responseJson.put("data", new JSONArray()); // Ensure "data" is always a valid JSON array
        }

        return responseJson.toString();
    }
//        @Override
//        public String toString() {
//            StringBuilder sb = new StringBuilder();
//            sb.append("Response:{");
//
//            if (status != false) {
//                sb.append("status:").append(status).append(", ");
//            }
//            if (code != 0) {
//                sb.append("code:").append(code).append(", ");
//            }
//            if (message != null && !message.isEmpty()) {
//                sb.append("message:'").append(message).append("', ");
//            }
//            if (obj_message != null && obj_message.length() > 0) {
//                sb.append("data:{");
//                for (int i = 0; i < obj_message.length(); i++) {
//                    JSONObject item = obj_message.getJSONObject(i);
//                    sb.append(item.toString());
//                    if (i < obj_message.length() - 1) {
//                        sb.append(", ");
//                    }
//                }
//                sb.append("}");
//            }
//
//            sb.append("}");
//            return sb.toString();
//        }


}
