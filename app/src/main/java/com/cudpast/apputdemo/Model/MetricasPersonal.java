package com.cudpast.apputdemo.Model;

public class MetricasPersonal {

    private String tempurature;
    private String so2;//saturacion
    private String pulse;
    private String symptoms;
    private String dateRegister;
    private String who_user_register;

    private Boolean s1, s2, s3, s4, s5, s6, s7;

    private Boolean testpruebarapida;

    private String namepaciente;
    private Boolean horario;

    public MetricasPersonal() {

    }

    public Boolean getHorario() {
        return horario;
    }

    public void setHorario(Boolean horario) {
        this.horario = horario;
    }

    public String getNamepaciente() {
        return namepaciente;
    }

    public void setNamepaciente(String namepaciente) {
        this.namepaciente = namepaciente;
    }

    public String getDateRegister() {
        return dateRegister;
    }

    public void setDateRegister(String dateRegister) {
        this.dateRegister = dateRegister;
    }

    public String getTempurature() {
        return tempurature;
    }

    public void setTempurature(String tempurature) {
        this.tempurature = tempurature;
    }

    public String getSo2() {
        return so2;
    }

    public void setSo2(String so2) {
        this.so2 = so2;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getWho_user_register() {
        return who_user_register;
    }

    public void setWho_user_register(String who_user_register) {
        this.who_user_register = who_user_register;
    }


    public Boolean getTestpruebarapida() {
        return testpruebarapida;
    }

    public void setTestpruebarapida(Boolean testpruebarapida) {
        this.testpruebarapida = testpruebarapida;
    }


    public Boolean getS1() {
        return s1;
    }

    public void setS1(Boolean s1) {
        this.s1 = s1;
    }

    public Boolean getS2() {
        return s2;
    }

    public void setS2(Boolean s2) {
        this.s2 = s2;
    }

    public Boolean getS3() {
        return s3;
    }

    public void setS3(Boolean s3) {
        this.s3 = s3;
    }

    public Boolean getS4() {
        return s4;
    }

    public void setS4(Boolean s4) {
        this.s4 = s4;
    }

    public Boolean getS5() {
        return s5;
    }

    public void setS5(Boolean s5) {
        this.s5 = s5;
    }

    public Boolean getS6() {
        return s6;
    }

    public void setS6(Boolean s6) {
        this.s6 = s6;
    }

    public Boolean getS7() {
        return s7;
    }

    public void setS7(Boolean s7) {
        this.s7 = s7;
    }
}
