package com.sonnets.sonnet.models;

import javax.validation.constraints.NotEmpty;

/**
 * DTO object handles input from controller. Only very basic input validation is done here. Bootstrap has front end,
 * implementation, but much more is needed.
 *
 * :TODO: add more lines.
 *
 * @author Josh Harkema
 */
public class SonnetDto {
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    private String title;
    private String publicationYear;
    private String line1;
    private String line2;
    private String line3;
    private String line4;
    private String line5;
    private String line6;
    private String line7;
    private String line8;
    private String line9;
    private String line10;
    private String line11;
    private String line12;
    private String line13;
    private String line14;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getLine3() {
        return line3;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
    }

    public String getLine4() {
        return line4;
    }

    public void setLine4(String line4) {
        this.line4 = line4;
    }

    public String getLine5() {
        return line5;
    }

    public void setLine5(String line5) {
        this.line5 = line5;
    }

    public String getLine6() {
        return line6;
    }

    public void setLine6(String line6) {
        this.line6 = line6;
    }

    public String getLine7() {
        return line7;
    }

    public void setLine7(String line7) {
        this.line7 = line7;
    }

    public String getLine8() {
        return line8;
    }

    public void setLine8(String line8) {
        this.line8 = line8;
    }

    public String getLine9() {
        return line9;
    }

    public void setLine9(String line9) {
        this.line9 = line9;
    }

    public String getLine10() {
        return line10;
    }

    public void setLine10(String line10) {
        this.line10 = line10;
    }

    public String getLine11() {
        return line11;
    }

    public void setLine11(String line11) {
        this.line11 = line11;
    }

    public String getLine12() {
        return line12;
    }

    public void setLine12(String line12) {
        this.line12 = line12;
    }

    public String getLine13() {
        return line13;
    }

    public void setLine13(String line13) {
        this.line13 = line13;
    }

    public String getLine14() {
        return line14;
    }

    public void setLine14(String line14) {
        this.line14 = line14;
    }

    @Override
    public String toString() {
        return "SonnetDto{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", title='" + title + '\'' +
                ", publicationYear=" + publicationYear +
                ", line1='" + line1 + '\'' +
                ", line2='" + line2 + '\'' +
                ", line3='" + line3 + '\'' +
                ", line4='" + line4 + '\'' +
                ", line5='" + line5 + '\'' +
                ", line6='" + line6 + '\'' +
                ", line7='" + line7 + '\'' +
                ", line8='" + line8 + '\'' +
                ", line9='" + line9 + '\'' +
                ", line10='" + line10 + '\'' +
                ", line11='" + line11 + '\'' +
                ", line12='" + line12 + '\'' +
                ", line13='" + line13 + '\'' +
                ", line14='" + line14 + '\'' +
                '}';
    }
}
