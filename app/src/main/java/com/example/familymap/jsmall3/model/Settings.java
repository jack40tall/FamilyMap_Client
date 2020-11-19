package com.example.familymap.jsmall3.model;

public class Settings {
    private boolean lifeStoryLines;
    private boolean familyTreeLines;
    private boolean spouseLines;
    private boolean fathersSideFilter;
    private boolean mothersSideFilter;
    private boolean MaleEventsFilter;
    private boolean FemaleEventsFilter;

    public Settings() {
        this.lifeStoryLines = false;
        this.familyTreeLines = false;
        this.spouseLines = false;
        this.fathersSideFilter = true;
        this.mothersSideFilter = true;
        MaleEventsFilter = true;
        FemaleEventsFilter = true;
    }

    public boolean isLifeStoryLines() {
        return lifeStoryLines;
    }

    public void setLifeStoryLines(boolean lifeStoryLines) {
        this.lifeStoryLines = lifeStoryLines;
    }

    public boolean isFamilyTreeLines() {
        return familyTreeLines;
    }

    public void setFamilyTreeLines(boolean familyTreeLines) {
        this.familyTreeLines = familyTreeLines;
    }

    public boolean isSpouseLines() {
        return spouseLines;
    }

    public void setSpouseLines(boolean spouseLines) {
        this.spouseLines = spouseLines;
    }

    public boolean isFathersSideFilter() {
        return fathersSideFilter;
    }

    public void setFathersSideFilter(boolean fathersSideFilter) {
        this.fathersSideFilter = fathersSideFilter;
    }

    public boolean isMothersSideFilter() {
        return mothersSideFilter;
    }

    public void setMothersSideFilter(boolean mothersSideFilter) {
        this.mothersSideFilter = mothersSideFilter;
    }

    public boolean isMaleEventsFilter() {
        return MaleEventsFilter;
    }

    public void setMaleEventsFilter(boolean maleEventsFilter) {
        MaleEventsFilter = maleEventsFilter;
    }

    public boolean isFemaleEventsFilter() {
        return FemaleEventsFilter;
    }

    public void setFemaleEventsFilter(boolean femaleEventsFilter) {
        FemaleEventsFilter = femaleEventsFilter;
    }

    public void reset() {
        this.lifeStoryLines = false;
        this.familyTreeLines = false;
        this.spouseLines = false;
        this.fathersSideFilter = true;
        this.mothersSideFilter = true;
        MaleEventsFilter = true;
        FemaleEventsFilter = true;
    }
}
