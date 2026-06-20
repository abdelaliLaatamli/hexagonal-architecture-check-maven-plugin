package co.alaatamli.plugins;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

class ArchitectureCheckMojoPackagingTest {

    private ArchitectureCheckMojo mojo;
    private MavenProject mockProject;
    private Log mockLog;

    @BeforeEach
    void setUp() throws Exception {
        mojo = new ArchitectureCheckMojo();
        mockProject = mock(MavenProject.class);
        mockLog = mock(Log.class);

        // Inject the mock Log into the Mojo
        mojo.setLog(mockLog);

        // Since 'project' field is private and has no setter, we inject it using reflection
        Field projectField = ArchitectureCheckMojo.class.getDeclaredField("project");
        projectField.setAccessible(true);
        projectField.set(mojo, mockProject);
    }

    @Test
    void should_SkipExecution_When_PackagingIsPom() throws MojoFailureException {
        // Arrange
        when(mockProject.getPackaging()).thenReturn("pom");

        // Act
        mojo.execute();

        // Assert: Verify it logged the skip message and never tried to read the build directory
        verify(mockLog).info("Skipping architecture check: packaging 'pom' is not supported.");
        verify(mockProject, never()).getBuild();
    }

    @Test
    void should_SkipExecution_When_PackagingIsNull() throws MojoFailureException {
        // Arrange
        when(mockProject.getPackaging()).thenReturn(null);

        // Act
        mojo.execute();

        // Assert
        verify(mockLog).info("Skipping architecture check: packaging 'null' is not supported.");
        verify(mockProject, never()).getBuild();
    }

    @Test
    void should_ProceedExecution_When_PackagingIsJarButNoClassesFound() throws MojoFailureException {
        // Arrange
        when(mockProject.getPackaging()).thenReturn("jar");

        // Mock the build to return a dummy path so the rest of the mojo runs
        org.apache.maven.model.Build mockBuild = mock(org.apache.maven.model.Build.class);
        when(mockProject.getBuild()).thenReturn(mockBuild);
        when(mockBuild.getOutputDirectory()).thenReturn("non_existent_directory_for_test");

        // Act
        mojo.execute();

        // Assert: It bypassed the packaging check and moved to the class directory check
        verify(mockLog).warn("No compiled classes found. Skipping architecture check.");
        verify(mockLog, never()).info(contains("is not supported"));
    }
}

