/*
 *    Copyright 2010 The myBatis Team
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.i2m;

import java.io.File;

import com.beust.jcommander.Parameter;

/**
 * 
 * @version $Id$
 */
public final class Options {

    @Parameter(
        names = { "-s", "--source" },
        description = "The XML Sql Map source dir",
        converter = FileConverter.class
    )
    private File source;

    @Parameter(
        names = { "-d", "--dest" },
        description = "The XML Mapper destination dir",
        converter = FileConverter.class
    )
    private File dest;

    @Parameter(
        names = { "-T", "--threads" },
        description = "The XML Mapper destination dir"
    )
    private int threads = 1;

    @Parameter(
        names = { "-h", "--help" },
        description = "Display help information."
    )
    private boolean help;

    @Parameter(
        names = { "-v", "--version" },
        description = "Display version information."
    )
    private boolean printVersion;

    public File getSource() {
        return source;
    }

    public void setSource(File source) {
        this.source = source;
    }

    public File getDest() {
        return dest;
    }

    public void setDest(File dest) {
        this.dest = dest;
    }

    public int getThreads() {
        return this.threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public boolean isHelp() {
        return help;
    }

    public boolean isPrintVersion()
    {
        return printVersion;
    }

    public void setPrintVersion( boolean printVersion )
    {
        this.printVersion = printVersion;
    }

}
