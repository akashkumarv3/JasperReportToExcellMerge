package com.javahowtos.jasperdemo.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ifsp {
	public ProgramDetails programDetails;
    private List<SampleBean> sampleBeans;
    private Map<String, Object> parameters;

    // Default constructor
    public Ifsp() {
    }

    // Getters and Setters
    public List<SampleBean> getSampleBeans() {
        return sampleBeans;
    }

    public void setSampleBeans(List<SampleBean> sampleBeans) {
        this.sampleBeans = sampleBeans;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }


}

