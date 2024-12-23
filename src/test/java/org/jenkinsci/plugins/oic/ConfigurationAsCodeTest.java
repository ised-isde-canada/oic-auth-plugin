package org.jenkinsci.plugins.oic;

import static io.jenkins.plugins.casc.misc.Util.getJenkinsRoot;
import static io.jenkins.plugins.casc.misc.Util.toStringFromYamlFile;
import static io.jenkins.plugins.casc.misc.Util.toYamlString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import hudson.security.SecurityRealm;
import io.jenkins.plugins.casc.ConfigurationContext;
import io.jenkins.plugins.casc.ConfiguratorRegistry;
import io.jenkins.plugins.casc.misc.ConfiguredWithCode;
import io.jenkins.plugins.casc.misc.JenkinsConfiguredWithCodeRule;
import io.jenkins.plugins.casc.model.CNode;
import jenkins.model.Jenkins;

public class ConfigurationAsCodeTest {

    @Rule
    public JenkinsConfiguredWithCodeRule chain = new JenkinsConfiguredWithCodeRule();

    @Test
    @ConfiguredWithCode("ConfigurationAsCode.yml")
    public void testConfig() {
        SecurityRealm realm = Jenkins.getInstance().getSecurityRealm();

        assertTrue(realm instanceof OicSecurityRealm);
        OicSecurityRealm oicSecurityRealm = (OicSecurityRealm) realm;

        assertEquals("http://localhost", oicSecurityRealm.getAuthorizationServerUrl());
        assertEquals("clientId", oicSecurityRealm.getClientId());
        assertEquals("clientSecret", oicSecurityRealm.getClientSecret().getPlainText());
        assertTrue(oicSecurityRealm.isDisableSslVerification());
        assertEquals("emailFieldName", oicSecurityRealm.getEmailFieldName());
        assertTrue(oicSecurityRealm.isEscapeHatchEnabled());
        assertEquals("escapeHatchGroup", oicSecurityRealm.getEscapeHatchGroup());
        assertEquals("escapeHatchSecret", oicSecurityRealm.getEscapeHatchSecret().getPlainText());
        assertEquals("escapeHatchUsername", oicSecurityRealm.getEscapeHatchUsername());
        assertEquals("fullNameFieldName", oicSecurityRealm.getFullNameFieldName());
        assertEquals("groupsFieldName", oicSecurityRealm.getGroupsFieldName());
        assertTrue(oicSecurityRealm.isLogoutFromOpenidProvider());
        assertEquals("scopes", oicSecurityRealm.getScopes());
        assertEquals("http://localhost", oicSecurityRealm.getTokenServerUrl());
        assertEquals("userNameField", oicSecurityRealm.getUserNameField());
    }

    @Test
    @ConfiguredWithCode("ConfigurationAsCode.yml")
    public void testExport() throws Exception {
        ConfigurationContext context = new ConfigurationContext(ConfiguratorRegistry.get());

        CNode yourAttribute = getJenkinsRoot(context).get("securityRealm").asMapping().get("oic");

        String exported = toYamlString(yourAttribute);

        // secrets are always changing. so, just remove them before there's a better solution
        String[] lines = exported.split("\n");
        List<String> lineList = new ArrayList<>();
        for (String line : lines) {
            if (!line.contains("Secret")) {
                lineList.add(line);
            }
        }
        String cleanedExported = String.join("\n", lineList);
        String expected = toStringFromYamlFile(this, "ConfigurationAsCodeExport.yml");

        assertThat(cleanedExported, is(expected));
    }
}
