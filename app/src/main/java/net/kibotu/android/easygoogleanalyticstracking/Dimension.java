package net.kibotu.android.easygoogleanalyticstracking;

public final class Dimension {

    public final int index;
    public final String value;

    public Dimension(final int index, final String value) {
        this.index = index;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dimension dimension = (Dimension) o;

        if (index != dimension.index) return false;
        return value != null ? value.equals(dimension.value) : dimension.value == null;

    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Dimension{" +
                "index=" + index +
                ", value='" + value + '\'' +
                '}';
    }
}