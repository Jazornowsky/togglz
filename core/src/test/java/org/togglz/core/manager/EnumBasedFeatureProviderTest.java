package org.togglz.core.manager;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;
import org.togglz.core.Feature;
import org.togglz.core.annotation.InfoLink;
import org.togglz.core.annotation.Label;
import org.togglz.core.annotation.Owner;
import org.togglz.core.metadata.FeatureMetaData;
import org.togglz.core.spi.FeatureProvider;

public class EnumBasedFeatureProviderTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForNull() {
        new EnumBasedFeatureProvider(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForNonEnumType() {
        new EnumBasedFeatureProvider(NotAnEnum.class);
    }

    @Test
    public void shouldReturnCorrectListOfFeaturesForEnum() {

        FeatureProvider provider = new EnumBasedFeatureProvider(ValidFeatureEnum.class);
        assertThat(provider.getFeatures())
            .containsSequence(ValidFeatureEnum.FEATURE1, ValidFeatureEnum.FEATURE2);

    }

    @Test
    public void shouldReturnMetaDataWithCorrectLabel() {

        FeatureProvider provider = new EnumBasedFeatureProvider(ValidFeatureEnum.class);
        FeatureMetaData metaData = provider.getMetaData(ValidFeatureEnum.FEATURE1);
        assertThat(metaData.getLabel()).isEqualTo("First feature");

    }

    @Test
    public void shouldReturnMetaDataWhenRequestedWithOtherFeatureImplementation() {

        FeatureProvider provider = new EnumBasedFeatureProvider(ValidFeatureEnum.class);
        FeatureMetaData metaData =
            provider.getMetaData(new OtherFeatureImpl(ValidFeatureEnum.FEATURE1.name()));
        assertThat(metaData.getLabel()).isEqualTo("First feature");

    }

    @Test
    public void shouldReturnOwnerNameIfAnnotationPresent() {
        FeatureProvider provider = new EnumBasedFeatureProvider(ValidFeatureEnum.class);
        FeatureMetaData metaData = provider.getMetaData(ValidFeatureEnum.WITH_OWNER);
        assertThat(metaData.getOwner()).isEqualTo("Christian");
    }

    @Test
    public void shouldReturnNullForOwnerNameByDefault() {
        FeatureProvider provider = new EnumBasedFeatureProvider(ValidFeatureEnum.class);
        FeatureMetaData metaData = provider.getMetaData(ValidFeatureEnum.FEATURE1);
        assertThat(metaData.getOwner()).isNull();
    }

    @Test
    public void shouldReturnInfoLinkIfAnnotationPresent() {
        FeatureProvider provider = new EnumBasedFeatureProvider(ValidFeatureEnum.class);
        FeatureMetaData metaData = provider.getMetaData(ValidFeatureEnum.WITH_LINK);
        assertThat(metaData.getInfoLink()).isEqualTo("https://github.com/togglz/togglz/pull/33");
    }

    @Test
    public void shouldReturnNullForInfoLinkByDefault() {
        FeatureProvider provider = new EnumBasedFeatureProvider(ValidFeatureEnum.class);
        FeatureMetaData metaData = provider.getMetaData(ValidFeatureEnum.FEATURE1);
        assertThat(metaData.getInfoLink()).isNull();
    }

    private static class NotAnEnum implements Feature {

        @Override
        public String name() {
            return "something";
        }

    }

    private static enum ValidFeatureEnum implements Feature {

        @Label("First feature")
        FEATURE1,

        FEATURE2,

        @Owner("Christian")
        WITH_OWNER,

        @InfoLink("https://github.com/togglz/togglz/pull/33")
        WITH_LINK;

    }

    private class OtherFeatureImpl implements Feature {

        private final String name;

        public OtherFeatureImpl(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

    }

}
