package com.zeyad.usecases.requests;

import android.support.annotation.Nullable;

import com.zeyad.usecases.TestRealmModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(JUnit4.class)
public class FileIORequestTest {

    private final String URL = "www.google.com";
    private final File FILE = Mockito.mock(File.class);
    private final Class DATA_CLASS = TestRealmModel.class;
    @Nullable
    private FileIORequest mFileIORequest;

    @Before
    public void setUp() throws Exception {
        mFileIORequest = new FileIORequest.Builder(URL)
                .file(FILE)
                .responseType(DATA_CLASS)
                .build();
    }

    @After
    public void tearDown() throws Exception {
        mFileIORequest = null;
    }

    @Test
    public void testGetUrl() throws Exception {
        assertThat(mFileIORequest.getUrl(), is(equalTo(URL)));
    }

    @Test
    public void testGetFile() throws Exception {
        assertThat(mFileIORequest.getFile(), is(equalTo(FILE)));
    }
}
