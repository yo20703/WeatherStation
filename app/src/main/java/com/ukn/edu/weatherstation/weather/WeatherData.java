package com.ukn.edu.weatherstation.weather;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherData {

    @SerializedName("success")
    private String success;

    @SerializedName("result")
    private Result result;

    @SerializedName("records")
    private Records records;
    public String getSuccess() {
        return success;
    }

    public Result getResult() {
        return result;
    }

    public Records getRecords() { return records; }

    public static class Result {
        @SerializedName("resource_id")
        private String resourceId;

        @SerializedName("fields")
        private List<Field> fields;


        public String getResourceId() {
            return resourceId;
        }

        public List<Field> getFields() {
            return fields;
        }

    }

    public static class Field {
        @SerializedName("id")
        private String id;

        @SerializedName("type")
        private String type;

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }
    }

    public static class Records {
        @SerializedName("datasetDescription")
        private String datasetDescription;

        @SerializedName("location")
        private List<Location> location;

        public String getDatasetDescription() {
            return datasetDescription;
        }

        public List<Location> getLocation() {
            return location;
        }
    }

    public static class Location {
        @SerializedName("locationName")
        private String locationName;

        @SerializedName("weatherElement")
        private List<WeatherElement> weatherElement;

        public String getLocationName() {
            return locationName;
        }

        public List<WeatherElement> getWeatherElement() {
            return weatherElement;
        }
    }

    public static class WeatherElement {
        @SerializedName("elementName")
        private String elementName;

        @SerializedName("time")
        private List<Time> time;

        public String getElementName() {
            return elementName;
        }

        public List<Time> getTime() {
            return time;
        }
    }

    public static class Time {
        @SerializedName("startTime")
        private String startTime;

        @SerializedName("endTime")
        private String endTime;

        @SerializedName("parameter")
        private Parameter parameter;

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public Parameter getParameter() {
            return parameter;
        }
    }

    public static class Parameter {
        @SerializedName("parameterName")
        private String parameterName;

        @SerializedName("parameterValue")
        private String parameterValue;

        @SerializedName("parameterUnit")
        private String parameterUnit;

        public String getParameterName() {
            return parameterName;
        }

        public String getParameterValue() {
            return parameterValue;
        }

        public String getParameterUnit() { return parameterUnit; }
    }
}

